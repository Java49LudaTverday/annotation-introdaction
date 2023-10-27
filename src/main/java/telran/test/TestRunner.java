package telran.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import telran.test.annotation.*;


public class TestRunner implements Runnable {
	private Object testObj;
	private List<Method> testMethods = new ArrayList<>();
	private List<Method> beforeEachMethods = new ArrayList<>();

	public TestRunner(Object testObj) {
		this.testObj = testObj;
	}

	@Override
	public void run() {
		Class<?> clazz = testObj.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		setMethodsByAnnotation(methods);
		launchAllMethods();
	}
	
	private void setMethodsByAnnotation(Method[] methods) {
		for (Method m : methods) {
			if (m.isAnnotationPresent(Test.class)) {				
					testMethods.add(m);
			}
			if(m.isAnnotationPresent(BeforeEach.class)) {
				beforeEachMethods.add(m);
			}
		}
	}

	private void launchAllMethods() {
		if(!testMethods.isEmpty()) {
			for(Method m: testMethods) {
			if(beforeEachMethods.size() != 0) {
				lauchMethodsByAnnotation(beforeEachMethods);
			}
			try {
				m.setAccessible(true);
				m.invoke(testObj);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
		}		
	}


	private void lauchMethodsByAnnotation(List<Method> methods) {
		for(Method method: methods) {
			try {
				method.setAccessible(true);
				method.invoke(testObj);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}


	
}
