/**
 * Copyright (C) 2010-2011 eBusiness Information, Excilys Group
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
 * 
 * @author Nabil Hachicha
 */
package com.googlecode.androidannotations.processing;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr._null;
import static com.sun.codemodel.JExpr._super;
import static com.sun.codemodel.JExpr.cast;
import static com.sun.codemodel.JExpr.invoke;
import static com.sun.codemodel.JExpr.lit;
import static com.sun.codemodel.JExpr.ref;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.HttpsClient;
import com.googlecode.androidannotations.annotations.Id;
import com.googlecode.androidannotations.processing.EBeansHolder.Classes;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class HttpsClientProcessor implements ElementProcessor {

	@Override
	public Class<? extends Annotation> getTarget() {
		return HttpsClient.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {
		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);

		HttpsClient annotation = element.getAnnotation(HttpsClient.class);
		int trustStoreFile = annotation.trustStore();
		String trustStorePwd = annotation.trustStorePwd();

		int keyStoreFile = annotation.keyStore();
		String keyStorePwd = annotation.keyStorePwd();

		boolean hostnameVerif = annotation.hostnameVerif();

		boolean useCustomTrustStore = Id.DEFAULT_VALUE != trustStoreFile ? true : false;
		boolean useCustomKeyStore = Id.DEFAULT_VALUE != keyStoreFile ? true : false;

		String fieldName = element.getSimpleName().toString();
		JBlock methodBody = holder.init.body();

		Classes classes = holder.classes();

		JDefinedClass jAnonClass = codeModel.anonymousClass(classes.DEFAULT_HTTP_CLIENT);

		JMethod method = jAnonClass.method(JMod.PROTECTED, classes.CLIENT_CONNECTION_MANAGER, "createClientConnectionManager");
		method.annotate(Override.class);

		JTryBlock jTryBlock = method.body()._try();
		JVar jVarTrusted = null;
		JVar jVarKeystore = null;

		if (useCustomKeyStore) {
			jVarKeystore = jTryBlock.body().decl(classes.KEY_STORE, "keystore");
			jVarKeystore.init(classes.KEY_STORE.staticInvoke("getInstance").arg("BKS"));
		}

		if (useCustomTrustStore || !useCustomTrustStore && useCustomKeyStore) {
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
			JInvocation jInvRawKey = jVarRes.invoke("openRawResource").arg(lit(keyStoreFile));
			jVarKeyFile = jTryBlock.body().decl(classes.INPUT_STREAM, "inKeystore", jInvRawKey);
		}

		if (useCustomTrustStore) {
			JInvocation jInvRawTrust = jVarRes.invoke("openRawResource").arg(lit(trustStoreFile));
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

			if (useCustomTrustStore || !useCustomTrustStore && useCustomKeyStore) {
				jTryLoad.body().add(invoke(jVarTrusted, "load").arg(jVarTrstFile).arg(invoke(lit(trustStorePwd), "toCharArray")));
			}
			// finally load
			JBlock jFinally = jTryLoad._finally();
			if (useCustomKeyStore) {
				jFinally.add(invoke(jVarKeyFile, "close"));
			}

			if (useCustomTrustStore || !useCustomTrustStore && useCustomKeyStore) {
				jFinally.add(invoke(jVarTrstFile, "close"));
			}
		}

		if (null == jVarKeystore && null == jVarTrusted) {
			JVar jVarCcm = jTryBlock.body().decl(classes.CLIENT_CONNECTION_MANAGER, "ccm");
			jVarCcm.init(_super().invoke("createClientConnectionManager"));

			if (!hostnameVerif) {
				JExpression jCast = cast(classes.SSL_SOCKET_FACTORY, jVarCcm.invoke("getSchemeRegistry").invoke("getScheme").arg("https").invoke("getSocketFactory"));
				jTryBlock.body().add(jCast.invoke("setHostnameVerifier").arg(classes.SSL_SOCKET_FACTORY.staticRef("ALLOW_ALL_HOSTNAME_VERIFIER")));
			}

			jTryBlock.body()._return(jVarCcm);

		} else {
			JVar jVarSslFact = jTryBlock.body().decl(classes.SSL_SOCKET_FACTORY, "newSslSocketFactory");
			jVarSslFact.init(_new(classes.SSL_SOCKET_FACTORY).arg(null == jVarKeystore ? _null() : jVarKeystore).arg(keyStorePwd).arg(null == jVarTrusted ? _null() : jVarTrusted));

			if (!hostnameVerif) {
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
