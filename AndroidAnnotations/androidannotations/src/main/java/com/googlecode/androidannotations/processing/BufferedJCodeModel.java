package com.googlecode.androidannotations.processing;

import java.util.ArrayList;
import java.util.List;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;

/**
 * This class is a decorator of {@link JCodeModel} allowing usage of
 * {@link #parseType(String)} and keeping each {@link JClass} found into the
 * {@link EBeansHolder} buffer. This is useful for generics definition.
 * <p />
 * <b>Note</b> : It's a hard-copy of a methods subset of {@link JCodeModel} with
 * just one custom line (in {@link TypeNameParser#parseTypeName()} :
 * 
 * <pre>
 * JClass clazz = eBeansHolder.uniqueRefClass(s.substring(start, idx));
 * </pre>
 * 
 * This solution was needed because JCodeModel is a final class.
 */
public class BufferedJCodeModel {

	private final EBeansHolder eBeansHolder;
	private final JCodeModel codeModel;

	public BufferedJCodeModel(EBeansHolder eBeansHolder, JCodeModel codeModel) {
		this.eBeansHolder = eBeansHolder;
		this.codeModel = codeModel;
	}

	/**
	 * Obtains a type object from a type name.
	 * 
	 * <p>
	 * This method handles primitive types, arrays, and existing {@link Class}
	 * es.
	 * 
	 * @exception ClassNotFoundException
	 *                If the specified type is not found.
	 */
	JType parseType(String name) throws ClassNotFoundException {
		// array
		if (name.endsWith("[]")) {
			return parseType(name.substring(0, name.length() - 2)).array();
		}

		// try primitive type
		try {
			return JType.parse(codeModel, name);
		} catch (IllegalArgumentException e) {
			;
		}

		// existing class
		return new TypeNameParser(name).parseTypeName();
	}

	private final class TypeNameParser {
		private final String s;
		private int idx;

		public TypeNameParser(String s) {
			this.s = s;
		}

		/**
		 * Parses a type name token T (which can be potentially of the form
		 * Tr&ly;T1,T2,...>, or "? extends/super T".)
		 * 
		 * @return the index of the character next to T.
		 */
		JClass parseTypeName() throws ClassNotFoundException {
			int start = idx;

			if (s.charAt(idx) == '?') {
				// wildcard
				idx++;
				ws();
				String head = s.substring(idx);
				if (head.startsWith("extends")) {
					idx += 7;
					ws();
					return parseTypeName().wildcard();
				} else if (head.startsWith("super")) {
					throw new UnsupportedOperationException("? super T not implemented");
				} else {
					// not supported
					throw new IllegalArgumentException("only extends/super can follow ?, but found " + s.substring(idx));
				}
			}

			while (idx < s.length()) {
				char ch = s.charAt(idx);
				if (Character.isJavaIdentifierStart(ch) || Character.isJavaIdentifierPart(ch) || ch == '.') {
					idx++;
				} else {
					break;
				}
			}

			JClass clazz = eBeansHolder.uniqueRefClass(s.substring(start, idx));

			return parseSuffix(clazz);
		}

		/**
		 * Parses additional left-associative suffixes, like type arguments and
		 * array specifiers.
		 */
		private JClass parseSuffix(JClass clazz) throws ClassNotFoundException {
			if (idx == s.length()) {
				return clazz; // hit EOL
			}

			char ch = s.charAt(idx);

			if (ch == '<') {
				return parseSuffix(parseArguments(clazz));
			}

			if (ch == '[') {
				if (s.charAt(idx + 1) == ']') {
					idx += 2;
					return parseSuffix(clazz.array());
				}
				throw new IllegalArgumentException("Expected ']' but found " + s.substring(idx + 1));
			}

			return clazz;
		}

		/**
		 * Skips whitespaces
		 */
		private void ws() {
			while (Character.isWhitespace(s.charAt(idx)) && idx < s.length()) {
				idx++;
			}
		}

		/**
		 * Parses '&lt;T1,T2,...,Tn>'
		 * 
		 * @return the index of the character next to '>'
		 */
		private JClass parseArguments(JClass rawType) throws ClassNotFoundException {
			if (s.charAt(idx) != '<') {
				throw new IllegalArgumentException();
			}
			idx++;

			List<JClass> args = new ArrayList<JClass>();

			while (true) {
				args.add(parseTypeName());
				if (idx == s.length()) {
					throw new IllegalArgumentException("Missing '>' in " + s);
				}
				char ch = s.charAt(idx);
				if (ch == '>') {
					return rawType.narrow(args.toArray(new JClass[args.size()]));
				}

				if (ch != ',') {
					throw new IllegalArgumentException(s);
				}
				idx++;
			}

		}
	}
}
