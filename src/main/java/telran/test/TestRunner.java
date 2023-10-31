package telran.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import telran.test.annotation.*;

public class TestRunner implements Runnable {
	private Object testObj;
	

	public TestRunner(Object testObj) {
		this.testObj = testObj;
	}

	@Override
	public void run() {
		Class<?> clazz = testObj.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		Method[] beforeEachMethods = getBaforeEachMethods(methods);
		runTestMethods(methods, beforeEachMethods);
	}

	private void runTestMethods(Method[] methods, Method[] beforeEachMethods) {
		for (Method m : methods) {
			if (m.isAnnotationPresent(Test.class)) {
				m.setAccessible(true);
				runMethods(beforeEachMethods);
				try {
					m.invoke(testObj);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void runMethods(Method[] methods) {
		for (Method method : methods) {
			try {
				method.setAccessible(true);
				method.invoke(testObj);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private Method[] getBaforeEachMethods(Method[] methods) {

		return Arrays.stream(methods).filter(m -> m.isAnnotationPresent(BeforeEach.class)).toArray(Method[]::new);
	}
}
