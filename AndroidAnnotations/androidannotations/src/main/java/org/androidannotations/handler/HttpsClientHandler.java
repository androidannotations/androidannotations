package org.androidannotations.handler;

import com.sun.codemodel.*;
import org.androidannotations.annotations.HttpsClient;
import org.androidannotations.annotations.ResId;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRInnerClass;
import org.androidannotations.validation.IsValid;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import static com.sun.codemodel.JExpr.*;

public class HttpsClientHandler extends BaseAnnotationHandler<EComponentHolder> {

	public HttpsClientHandler(ProcessingEnvironment processingEnvironment) {
		super(HttpsClient.class, processingEnvironment);
	}

	@Override
	public boolean validate(Element element, AnnotationElements validatedElements) {
		IsValid valid = new IsValid();

		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validatedElements, valid);

		validatorHelper.annotationParameterIsOptionalValidResId(element, IRClass.Res.RAW, "keyStore", valid);
		validatorHelper.annotationParameterIsOptionalValidResId(element, IRClass.Res.RAW, "trustStore", valid);

		validatorHelper.isNotPrivate(element, valid);

		return valid.isValid();
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		HttpsClient annotation = element.getAnnotation(HttpsClient.class);
		int trustStoreRawId = annotation.trustStore();
		String trustStorePwd = annotation.trustStorePwd();
		int keyStoreRawId = annotation.keyStore();
		String keyStorePwd = annotation.keyStorePwd();

		boolean allowAllHostnames = annotation.allowAllHostnames();
		boolean useCustomTrustStore = ResId.DEFAULT_VALUE != trustStoreRawId;
		boolean useCustomKeyStore = ResId.DEFAULT_VALUE != keyStoreRawId;

		String fieldName = element.getSimpleName().toString();
		JBlock methodBody = holder.getInit().body();

		ProcessHolder.Classes classes = holder.classes();

		JDefinedClass jAnonClass = holder.codeModel().anonymousClass(classes.DEFAULT_HTTP_CLIENT);

		JMethod method = jAnonClass.method(JMod.PROTECTED, classes.CLIENT_CONNECTION_MANAGER, "createClientConnectionManager");
		method.annotate(Override.class);

		JTryBlock jTryBlock = method.body()._try();
		JVar jVarTrusted = null;
		JVar jVarKeystore = null;

		if (useCustomKeyStore) {
			jVarKeystore = jTryBlock.body().decl(classes.KEY_STORE, "keystore");
			jVarKeystore.init(classes.KEY_STORE.staticInvoke("getInstance").arg("BKS"));
		}

		if (useCustomTrustStore || useCustomKeyStore) {
			/*
			 * use default trust store
			 */
			jVarTrusted = jTryBlock.body().decl(classes.KEY_STORE, "trusted");
			jVarTrusted.init(classes.KEY_STORE.staticInvoke("getInstance").arg("BKS"));

		}

		JVar jVarRes = null;
		JVar jVarTrstFile = null;
		JVar jVarKeyFile = null;

		if (useCustomKeyStore || useCustomTrustStore) {
			jVarRes = jTryBlock.body().decl(classes.RESOURCES, "res", invoke("getResources"));
		}

		IRInnerClass rInnerClass = rClass.get(IRClass.Res.RAW);
		if (useCustomKeyStore) {
			JFieldRef rawIdRef = rInnerClass.getIdStaticRef(keyStoreRawId, holder);
			JInvocation jInvRawKey = jVarRes.invoke("openRawResource").arg(rawIdRef);
			jVarKeyFile = jTryBlock.body().decl(classes.INPUT_STREAM, "inKeystore", jInvRawKey);
		}

		if (useCustomTrustStore) {
			JFieldRef rawIdRef = rInnerClass.getIdStaticRef(trustStoreRawId, holder);
			JInvocation jInvRawTrust = jVarRes.invoke("openRawResource").arg(rawIdRef);
			jVarTrstFile = jTryBlock.body().decl(classes.INPUT_STREAM, "inTrustStore", jInvRawTrust);

		} else if (useCustomKeyStore) {
			jVarTrstFile = jTryBlock.body().decl(classes.INPUT_STREAM, "inTrustStore", _new(classes.FILE_INPUT_STREAM).arg("/system/etc/security/cacerts.bks"));
		}

		// try load
		if (useCustomKeyStore || useCustomTrustStore) {
			JTryBlock jTryLoad = jTryBlock.body()._try();

			if (useCustomKeyStore) {
				jTryLoad.body().add(invoke(jVarKeystore, "load").arg(jVarKeyFile).arg(invoke(lit(keyStorePwd), "toCharArray")));
			}

			jTryLoad.body().add(invoke(jVarTrusted, "load").arg(jVarTrstFile).arg(invoke(lit(trustStorePwd), "toCharArray")));

			// finally load
			JBlock jFinally = jTryLoad._finally();
			if (useCustomKeyStore) {
				jFinally.add(invoke(jVarKeyFile, "close"));
			}

			jFinally.add(invoke(jVarTrstFile, "close"));
		}

		if (null == jVarKeystore && null == jVarTrusted) {
			JVar jVarCcm = jTryBlock.body().decl(classes.CLIENT_CONNECTION_MANAGER, "ccm");
			jVarCcm.init(_super().invoke("createClientConnectionManager"));

			if (allowAllHostnames) {
				JExpression jCast = cast(classes.SSL_SOCKET_FACTORY, jVarCcm.invoke("getSchemeRegistry").invoke("getScheme").arg("https").invoke("getSocketFactory"));
				jTryBlock.body().add(jCast.invoke("setHostnameVerifier").arg(classes.SSL_SOCKET_FACTORY.staticRef("ALLOW_ALL_HOSTNAME_VERIFIER")));
			}

			jTryBlock.body()._return(jVarCcm);

		} else {
			JVar jVarSslFact = jTryBlock.body().decl(classes.SSL_SOCKET_FACTORY, "newSslSocketFactory");
			jVarSslFact.init(_new(classes.SSL_SOCKET_FACTORY).arg(null == jVarKeystore ? _null() : jVarKeystore).arg(keyStorePwd).arg(jVarTrusted));

			if (allowAllHostnames) {
				jTryBlock.body().add(invoke(jVarSslFact, "setHostnameVerifier").arg(classes.SSL_SOCKET_FACTORY.staticRef("ALLOW_ALL_HOSTNAME_VERIFIER")));
			}

			JVar jVarSchemeReg = jTryBlock.body().decl(classes.SCHEME_REGISTRY, "registry");
			jVarSchemeReg.init(_new(classes.SCHEME_REGISTRY));
			jTryBlock.body().add(invoke(jVarSchemeReg, "register").arg(_new(classes.SCHEME).arg("https").arg(jVarSslFact).arg(lit(443))));

			JVar jVarCcm = jTryBlock.body().decl(classes.CLIENT_CONNECTION_MANAGER, "ccm");
			jVarCcm.init(_new(classes.SINGLE_CLIENT_CONN_MANAGER).arg(invoke("getParams")).arg(jVarSchemeReg));
			jTryBlock.body()._return(jVarCcm);
		}

		// catch block
		JCatchBlock jCatchBlock = jTryBlock._catch(classes.EXCEPTION);
		JVar jVarExceptionParam = jCatchBlock.param("e");
		jCatchBlock.body().add(jVarExceptionParam.invoke("printStackTrace"));
		jCatchBlock.body()._return(_super().invoke("createClientConnectionManager"));

		methodBody.assign(ref(fieldName), _new(jAnonClass));
	}
}
