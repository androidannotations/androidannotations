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

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

import com.googlecode.androidannotations.annotations.HttpsClient;
import com.googlecode.androidannotations.annotations.Id;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JVar;

public class HttpsClientProcessor implements ElementProcessor {

	public HttpsClientProcessor() {
	}

	@Override
	public Class<? extends Annotation> getTarget() {
		return HttpsClient.class;
	}

	@Override
	public void process(Element element, JCodeModel codeModel, EBeansHolder eBeansHolder) {
		EBeanHolder holder = eBeansHolder.getEnclosingEBeanHolder(element);

		int trustStoreFile = element.getAnnotation(HttpsClient.class).trustStore();
		String trustStorePwd = element.getAnnotation(HttpsClient.class).trustStorePwd();

		int keyStoreFile = element.getAnnotation(HttpsClient.class).keyStore();
		String keyStorePwd = element.getAnnotation(HttpsClient.class).keyStorePwd();

		boolean hostnameVerif = element.getAnnotation(HttpsClient.class).hostnameVerif();

		boolean useCustomTrustStore = Id.DEFAULT_VALUE != trustStoreFile ? true : false;
		boolean useCustomKeyStore = Id.DEFAULT_VALUE != keyStoreFile ? true : false;

		String fieldName = element.getSimpleName().toString();
		JBlock methodBody = holder.init.body();

		JClass jTypeRetMethod = holder.refClass("org.apache.http.conn.ClientConnectionManager");
		JClass jTypeDefHttp = holder.refClass("org.apache.http.impl.client.DefaultHttpClient");
		JClass jTypeKeyStore = holder.refClass("java.security.KeyStore");
		JClass jTypeRes = holder.refClass("android.content.res.Resources");
		JClass jTypeInputStream = holder.refClass("java.io.InputStream");

		JDefinedClass jAnonClass = codeModel.anonymousClass(jTypeDefHttp);

		JMethod method = jAnonClass.method(JMod.PROTECTED, jTypeRetMethod, "createClientConnectionManager");
		method.annotate(Override.class);

		JTryBlock jTryBlock = method.body()._try();
		JVar jVarTrusted = null;
		JVar jVarKeystore = null;

		if (useCustomKeyStore) {
			jVarKeystore = jTryBlock.body().decl(jTypeKeyStore, "keystore");
			jVarKeystore.init(jTypeKeyStore.staticInvoke("getInstance").arg("BKS"));
		}

		if (useCustomTrustStore || !useCustomTrustStore && useCustomKeyStore/*
																			 * use
																			 * default
																			 * trust
																			 * store
																			 */) {
			jVarTrusted = jTryBlock.body().decl(jTypeKeyStore, "trusted");
			jVarTrusted.init(jTypeKeyStore.staticInvoke("getInstance").arg("BKS"));

		}

		JVar jVarRes = null;
		JVar jVarTrstFile = null;
		JVar jVarKeyFile = null;

		if (useCustomKeyStore || useCustomTrustStore) {
			jVarRes = jTryBlock.body().decl(jTypeRes, "res", JExpr.invoke("getResources"));
		}

		if (useCustomKeyStore) {
			JInvocation jInvRawKey = jVarRes.invoke("openRawResource").arg(JExpr.lit(keyStoreFile));
			jVarKeyFile = jTryBlock.body().decl(jTypeInputStream, "inKeystore", jInvRawKey);
		}

		if (useCustomTrustStore) {
			JInvocation jInvRawTrust = jVarRes.invoke("openRawResource").arg(JExpr.lit(trustStoreFile));
			jVarTrstFile = jTryBlock.body().decl(jTypeInputStream, "inTrustStore", jInvRawTrust);

		} else if (useCustomKeyStore) {
			JClass jTypeFileInputStream = holder.refClass("java.io.FileInputStream");
			jVarTrstFile = jTryBlock.body().decl(jTypeInputStream, "inTrustStore", JExpr._new(jTypeFileInputStream).arg("/system/etc/security/cacerts.bks"));
		}

		// try load
		if (useCustomKeyStore || useCustomTrustStore) {
			JTryBlock jTryLoad = jTryBlock.body()._try();

			if (useCustomKeyStore) {
				jTryLoad.body().add(JExpr.invoke(jVarKeystore, "load").arg(jVarKeyFile).arg(JExpr.invoke(JExpr.lit(keyStorePwd), "toCharArray")));
			}

			if (useCustomTrustStore || !useCustomTrustStore && useCustomKeyStore) {
				jTryLoad.body().add(JExpr.invoke(jVarTrusted, "load").arg(jVarTrstFile).arg(JExpr.invoke(JExpr.lit(trustStorePwd), "toCharArray")));
			}
			// finally load
			JBlock jFinally = jTryLoad._finally();
			if (useCustomKeyStore) {
				jFinally.add(JExpr.invoke(jVarKeyFile, "close"));
			}

			if (useCustomTrustStore || !useCustomTrustStore && useCustomKeyStore) {
				jFinally.add(JExpr.invoke(jVarTrstFile, "close"));
			}
		}

		if (null == jVarKeystore && null == jVarTrusted) {
			JVar jVarCcm = jTryBlock.body().decl(jTypeRetMethod, "ccm");
			jVarCcm.init(JExpr._super().invoke("createClientConnectionManager"));

			if (!hostnameVerif) {
				JClass jTypeSocketFact = holder.refClass("org.apache.http.conn.ssl.SSLSocketFactory");
				JExpression jCast = JExpr.cast(jTypeSocketFact, jVarCcm.invoke("getSchemeRegistry").invoke("getScheme").arg("https").invoke("getSocketFactory"));
				JClass jTypeSslFact = holder.refClass("org.apache.http.conn.ssl.SSLSocketFactory");
				jTryBlock.body().add(jCast.invoke("setHostnameVerifier").arg(jTypeSslFact.staticRef("ALLOW_ALL_HOSTNAME_VERIFIER")));
			}

			jTryBlock.body()._return(jVarCcm);

		} else {
			JClass jTypeSslFact = holder.refClass("org.apache.http.conn.ssl.SSLSocketFactory");
			JClass jTypeScheme = holder.refClass("org.apache.http.conn.scheme.Scheme");
			JClass jTypeSchemeReg = holder.refClass("org.apache.http.conn.scheme.SchemeRegistry");
			JClass jTypeSingleConnMgr = holder.refClass("org.apache.http.impl.conn.SingleClientConnManager");

			JVar jVarSslFact = jTryBlock.body().decl(jTypeSslFact, "newSslSocketFactory");
			jVarSslFact.init(JExpr._new(jTypeSslFact).arg(null == jVarKeystore ? JExpr._null() : jVarKeystore).arg(keyStorePwd).arg(null == jVarTrusted ? JExpr._null() : jVarTrusted));

			if (!hostnameVerif) {
				jTryBlock.body().add(JExpr.invoke(jVarSslFact, "setHostnameVerifier").arg(jTypeSslFact.staticRef("ALLOW_ALL_HOSTNAME_VERIFIER")));
			}

			JVar jVarSchemeReg = jTryBlock.body().decl(jTypeSchemeReg, "registry");
			jVarSchemeReg.init(JExpr._new(jTypeSchemeReg));
			jTryBlock.body().add(JExpr.invoke(jVarSchemeReg, "register").arg(JExpr._new(jTypeScheme).arg("https").arg(jVarSslFact).arg(JExpr.lit(443))));

			JVar jVarCcm = jTryBlock.body().decl(jTypeRetMethod, "ccm");
			jVarCcm.init(JExpr._new(jTypeSingleConnMgr).arg(JExpr.invoke("getParams")).arg(jVarSchemeReg));
			jTryBlock.body()._return(jVarCcm);
		}

		// catch block
		JClass jTypeException = holder.refClass("java.lang.Exception");
		JCatchBlock jCatchBlock = jTryBlock._catch(jTypeException);
		JVar jVarExceptionParam = jCatchBlock.param("e");
		jCatchBlock.body().add(jVarExceptionParam.invoke("printStackTrace"));
		jCatchBlock.body()._return(JExpr._super().invoke("createClientConnectionManager"));

		methodBody.assign(JExpr.ref(fieldName), JExpr._new(jAnonClass));

	}

}
