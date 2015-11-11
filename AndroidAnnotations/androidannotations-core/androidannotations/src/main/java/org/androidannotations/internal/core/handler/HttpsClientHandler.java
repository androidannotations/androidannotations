/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.internal.core.handler;

import static com.helger.jcodemodel.JExpr._new;
import static com.helger.jcodemodel.JExpr._null;
import static com.helger.jcodemodel.JExpr._super;
import static com.helger.jcodemodel.JExpr.cast;
import static com.helger.jcodemodel.JExpr.invoke;
import static com.helger.jcodemodel.JExpr.lit;
import static com.helger.jcodemodel.JExpr.ref;

import javax.lang.model.element.Element;

import org.androidannotations.AndroidAnnotationsEnvironment;
import org.androidannotations.ElementValidation;
import org.androidannotations.annotations.HttpsClient;
import org.androidannotations.handler.BaseAnnotationHandler;
import org.androidannotations.holder.EComponentHolder;
import org.androidannotations.internal.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;
import org.androidannotations.rclass.IRInnerClass;

import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCatchBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldRef;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JTryBlock;
import com.helger.jcodemodel.JVar;

public class HttpsClientHandler extends BaseAnnotationHandler<EComponentHolder> {

	public HttpsClientHandler(AndroidAnnotationsEnvironment environment) {
		super(HttpsClient.class, environment);
	}

	@Override
	public void validate(Element element, ElementValidation validation) {
		validatorHelper.enclosingElementHasEnhancedComponentAnnotation(element, validation);

		validatorHelper.annotationParameterIsOptionalValidResId(element, IRClass.Res.RAW, "keyStore", validation);
		validatorHelper.annotationParameterIsOptionalValidResId(element, IRClass.Res.RAW, "trustStore", validation);

		validatorHelper.isNotPrivate(element, validation);
	}

	@Override
	public void process(Element element, EComponentHolder holder) throws Exception {
		IRInnerClass rInnerClass = getEnvironment().getRClass().get(IRClass.Res.RAW);
		HttpsClient annotation = element.getAnnotation(HttpsClient.class);
		JFieldRef trustStoreRawIdRef = annotationHelper.extractOneAnnotationFieldRef(element, getTarget(), rInnerClass, false, "trustStore", "trustStoreResName");
		JFieldRef keyStoreRawIdRef = annotationHelper.extractOneAnnotationFieldRef(element, getTarget(), rInnerClass, false, "keyStore", "keyStoreResName");
		String trustStorePwd = annotation.trustStorePwd();
		String keyStorePwd = annotation.keyStorePwd();

		boolean allowAllHostnames = annotation.allowAllHostnames();
		boolean useCustomTrustStore = trustStoreRawIdRef != null;
		boolean useCustomKeyStore = keyStoreRawIdRef != null;

		String fieldName = element.getSimpleName().toString();
		JBlock methodBody = holder.getInitBodyInjectionBlock();

		ProcessHolder.Classes classes = getClasses();

		JDefinedClass jAnonClass = getCodeModel().anonymousClass(classes.DEFAULT_HTTP_CLIENT);

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

		if (useCustomKeyStore) {
			JInvocation jInvRawKey = jVarRes.invoke("openRawResource").arg(keyStoreRawIdRef);
			jVarKeyFile = jTryBlock.body().decl(classes.INPUT_STREAM, "inKeystore", jInvRawKey);
		}

		if (useCustomTrustStore) {
			JInvocation jInvRawTrust = jVarRes.invoke("openRawResource").arg(trustStoreRawIdRef);
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
				IJExpression jCast = cast(classes.SSL_SOCKET_FACTORY, jVarCcm.invoke("getSchemeRegistry").invoke("getScheme").arg("https").invoke("getSocketFactory"));
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
			jTryBlock.body().add(invoke(jVarSchemeReg, "register").arg(_new(classes.SCHEME).arg("http").arg(classes.PLAIN_SOCKET_FACTORY.staticInvoke("getSocketFactory")).arg(lit(80))));

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
