package exp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2014年4月16日 下午2:34:19
 */
public class Exp {
	
	public static void main(String[] args) {
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setBeanResolver(new BeanResolver(){
			@Override
			public Object resolve(EvaluationContext context, String beanName) throws AccessException {
				return new A("飞飞");
			}});
		// This will end up calling resolve(context,"foo") on MyBeanResolver during evaluation
		Object bean = parser.parseExpression("@foo").getValue(context);
		System.out.println(bean);
	}

	public static abstract class StringUtils {
		public static String reverseString(String input) {
			StringBuilder backwards = new StringBuilder();
			for (int i = 0; i < input.length(); i++)
				backwards.append(input.charAt(input.length() - 1 - i));
			return backwards.toString();
		}
	}
	
	public static void main13(String[] args) throws Exception {
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.registerFunction("reverseString", 
		                         StringUtils.class.getDeclaredMethod("reverseString", 
		                                                             new Class[] { String.class }));
		String helloWorldReversed = parser.parseExpression("#reverseString('hello')").getValue(context, String.class);
		System.out.println(helloWorldReversed);
	}

	public static void main12(String[] args) {
		List<Integer> primes = new ArrayList<Integer>();
		primes.addAll(Arrays.asList(2, 3, 5, 7, 11, 13, 17));
		// create parser and set variable 'primes' as the array of integers
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("primes", primes);
		// all prime numbers > 10 from the list (using selection ?{...})
		// evaluates to [11, 13, 17]
		List<Integer> primesGreaterThanTen = (List<Integer>) parser.parseExpression("#primes.?[#this>10]").getValue(
				context);
		System.out.println(primesGreaterThanTen);
	}

	public static void main9(String[] args) {
		ExpressionParser parser = new SpelExpressionParser();
		// evals to "Hello World"
		String helloWorld = (String) parser.parseExpression("'Hello World'").getValue();
		double avogadrosNumber = (Double) parser.parseExpression("6.0221415E+23").getValue();
		// evals to 2147483647
		int maxValue = (Integer) parser.parseExpression("0x7FFFFFFF").getValue();
		boolean trueValue = (Boolean) parser.parseExpression("true").getValue();
		Object nullValue = parser.parseExpression("null").getValue();

		System.out.println(helloWorld);
		System.out.println(avogadrosNumber);
		System.out.println(maxValue);
		System.out.println(trueValue);
		System.out.println(nullValue);

		// int year = (Integer)
		// parser.parseExpression("Birthdate.Year + 1900").getValue(context);
		// String city = (String)
		// parser.parseExpression("placeOfBirth.City").getValue(context);

	}

	public static void main8(String[] args) {
		class Simple {
			public List<Boolean> booleanList = new ArrayList<Boolean>();
		}
		Simple simple = new Simple();
		simple.booleanList.add(true);
		StandardEvaluationContext simpleContext = new StandardEvaluationContext(simple);
		// false is passed in here as a string. SpEL and the conversion service
		// will
		// correctly recognize that it needs to be a Boolean and convert it
		ExpressionParser parser = new SpelExpressionParser();
		parser.parseExpression("booleanList[0]").setValue(simpleContext, "false");
		// b will be false
		Boolean b = simple.booleanList.get(0);
		System.out.println(b);
	}

	public static void main11(String[] args) {
		ExpressionParser parser = new SpelExpressionParser();
		System.out.println(parser.parseExpression("new exp.Exp$A('刘飞')").getValue());
	}

	static class A {
		String name;

		public A(String name) {
			super();
			this.name = name;
		}

		public A() {
			super();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static void main7(String[] args) {
		A a = new A();
		a.name = "liufei";
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("name == 'Nikola Tesla'");
		EvaluationContext context = new StandardEvaluationContext(a);
		boolean result = exp.getValue(context, Boolean.class); // evaluates to
																// true
		System.out.println(result);
	}

	public static void main6(String[] args) {
		A a = new A();
		a.name = "liufei";
		GregorianCalendar c = new GregorianCalendar();
		c.set(1856, 7, 9);
		// The constructor arguments are name, birthday, and nationality.
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("name");
		String name = (String) exp.getValue(a);
		System.out.println(name);
	}

	public static void main5(String[] args) {
		GregorianCalendar c = new GregorianCalendar();
		c.set(1856, 7, 9);
		// The constructor arguments are name, birthday, and nationality.
		A a = new A();
		a.name = "liufei";
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("name");
		EvaluationContext context = new StandardEvaluationContext(a);
		String name = (String) exp.getValue(context);
		System.out.println(name);
	}

	public static void main4(String[] args) {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("new String('hello world').toUpperCase()");
		String message = exp.getValue(String.class);
		System.out.println(message);
	}

	public static void main3(String[] args) {
		ExpressionParser parser = new SpelExpressionParser();
		// invokes 'getBytes().length'
		Expression exp = parser.parseExpression("'Hello World'.bytes.length");
		int length = (Integer) exp.getValue();
		System.out.println(length);
	}

	public static void main2(String[] args) {
		ExpressionParser parser = new SpelExpressionParser();
		// invokes 'getBytes()'
		Expression exp = parser.parseExpression("'Hello World'.bytes");
		byte[] bytes = (byte[]) exp.getValue();
		System.out.println(new String(bytes));
	}

	public static void main1(String[] args) {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("'Hello World'.concat('!')");
		String message = (String) exp.getValue();
		System.out.println(message);
	}

	public static void main0(String[] args) {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("'Hello World'");
		String message = (String) exp.getValue();
		System.out.println(message);
	}
}
