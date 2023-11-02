package telran.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

import telran.configuration.annotation.Value;


public class Configuration {
	private Object obj;
	private Properties properties;


	public Configuration(Object obj) {
		this.obj = obj;
	}
	public Configuration(Object testObj, String fileName) throws Exception {
		obj = testObj;
		properties = new Properties();
		properties.load(new FileInputStream(fileName));
			
	}
	public void configInjection() {
		Field[] fields = obj.getClass().getDeclaredFields();
		Arrays.stream(fields).filter(f -> f.isAnnotationPresent(Value.class)).forEach(this::injection);
	}

	void injection(Field field) {
		
		String value = getValue(field);
		String convertionMethodName = getConvertionMethodName(field.getType().getSimpleName());
		try {
			Method method = this.getClass().getDeclaredMethod(convertionMethodName, String.class);
			Object convertedObject = method.invoke(this, value); 
			field.setAccessible(true);
			setValue(field, convertedObject);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private String getValue(Field field) {
		Value valueAnnotation = field.getAnnotation(Value.class);
		String property = valueAnnotation.value();
		String[] tokens = property.split(":");
		String defaultValue = "";
		if (tokens.length == 2) {
			defaultValue = tokens[1];
			property = tokens[0];
		}
		String value = properties.getProperty(property, defaultValue);
		return value;
	}

	private void setValue(Field field, Object convertedObject) throws IllegalAccessException {
		field.set(obj, convertedObject);
	
	}
	private String getConvertionMethodName(String type) {
		
		return type + "Convertion";
	}
	Integer intConvertion(String value) {
		return Integer.valueOf(value);
	}
	Long longConvertion(String value) {
		return Long.valueOf(value);
	}
	Float floatConvertion(String value) {
		return Float.valueOf(value);
	}
	Double doubleConvertion(String value) {
		return Double.valueOf(value);
	}
	String StringConvertion(String value) {
		return value;
	}
	

}
