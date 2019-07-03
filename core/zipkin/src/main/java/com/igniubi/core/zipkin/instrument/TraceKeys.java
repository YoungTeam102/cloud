/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.igniubi.core.zipkin.instrument;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;



@Component
public class TraceKeys {

	private Redis redis=new Redis();

	private Mysql mysql = new Mysql();


	public Redis getRedis() {
		return redis;
	}


	public void setRedis(Redis redis) {
		this.redis = redis;
	}


	public static class Redis{
		private String classNameKey = "class";

		private String methodNameKey = "method";

		public String getClassNameKey() {
			return classNameKey;
		}

		public String getMethodNameKey() {
			return methodNameKey;
		}

		public void setClassNameKey(String classNameKey) {
			this.classNameKey = classNameKey;
		}

		public void setMethodNameKey(String methodNameKey) {
			this.methodNameKey = methodNameKey;
		}
	}

	public static class Mysql{
		private String classNameKey = "class";

		private String methodNameKey = "method";

		public String getClassNameKey() {
			return classNameKey;
		}

		public String getMethodNameKey() {
			return methodNameKey;
		}

		public void setClassNameKey(String classNameKey) {
			this.classNameKey = classNameKey;
		}

		public void setMethodNameKey(String methodNameKey) {
			this.methodNameKey = methodNameKey;
		}
	}


}
