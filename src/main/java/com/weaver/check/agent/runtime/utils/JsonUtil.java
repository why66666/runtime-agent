package com.weaver.check.agent.runtime.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Json字符串操作.
 * <P>
 * 指定模式，NULL 对象不输出，或全输出等 1 字串转换对象<br>
 * 2 对象转换为json字符串<br>
 * 3 获得指定节点下的值<br>
 * 4 将json字符串转换为JsonNode对象<br>
 * 5 获取指定节点文本内容
 */

public final class JsonUtil {
	

	private JsonUtil() {
	}

	/**
	 * Writes object to the writer as JSON using Jackson and adds a new-line
	 * before flushing.
	 *
	 * @param writer
	 *            the writer to write the JSON to
	 * @param object
	 *            the object to write as JSON
	 * @throws IOException
	 *             if the object can't be serialized as JSON or written to the
	 *             writer
	 */
	public static void writeJson(Writer writer, Object object)
			throws IOException {
		mapper.writeValue(writer, object);
	}

	public static void writeJson(String fileName, Object object)
			throws IOException {
		Writer writer = new PrintWriter(fileName);
		try {
			JsonUtil.writeJson(writer, object);
		} finally {
			writer.close();
		}
	}

	/**
	 * Serializes object to JSON string.
	 *
	 * @param object
	 *            object to serialize.
	 * @return json string.
	 * @throws IOException
	 */
	public static String toJson(Object object) {
		try {
			StringWriter writer = new StringWriter();
			writeJson(writer, object);
			return writer.toString();
		} catch (IOException e) {
			System.out.println("parse object to json string error:");
		}
		return null;
	}

	/**
	 * Parse JSON string to object.
	 *
	 * @param json
	 *            string containing JSON.
	 * @param type
	 *            type reference describing type of object to parse from json.
	 * @param <T>
	 *            type of object to parse from json.
	 * @return object parsed from json.
	 * @throws IOException
	 */
	public static <T> T toObject(String json, TypeReference<T> type)
			throws IOException {
		try {
			return mapper.readValue(json, type);
		} catch (JsonParseException e) {
			throw new IOException(String.format("Failed to parse json '%s'",
					json), e);
		}
	}

	public static <T> T toObject(String json, JavaType type) throws IOException {
		return mapper.readValue(json, type);
	}
	
	public static <T> T toObject(String json, Class<T> clazz) throws IOException {
		return mapper.readValue(json, clazz);
	}
	
	public static Map<String, String> toMap(String json) throws IOException {
		return toObject(json, new TypeReference<Map<String, String>>(){});
	}
	
	public static Map<String, Object> toObjectMap(String json) throws IOException {
		return toObject(json, new TypeReference<Map<String, Object>>(){});
	}

	public static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());
			return Charset.defaultCharset().decode(bb).toString();
		} finally {
			stream.close();
		}
	}

	private static final ObjectMapper mapper = newMapper();

	private static ObjectMapper newMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT, false);
		mapper.disable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
		mapper.disable(SerializationFeature.CLOSE_CLOSEABLE);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		return mapper;
	}
	
}
