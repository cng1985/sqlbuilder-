package com.ada.sqlbuilder.handel;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ada.sqlbuilder.Model;
import com.ada.sqlbuilder.TableInfo;
import com.ada.sqlbuilder.dbutils.ResultSetHandler;

public class BeanListMHandler<T extends Model> implements
		ResultSetHandler<List<T>> {

	@Override
	public List<T> handle(ResultSet rs) throws SQLException {
		List<T> results = new ArrayList<T>();

		if (!rs.next()) {
			return results;
		}

		do {
			results.add(this.createBean(rs, type));
		} while (rs.next());

		return results;
	}

	private T createBean(ResultSet rs, Class<T> type2) {
		T result = null;
		try {
			result = type.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		for (Field field : tableInfo.getFields()) {
			final String fieldName = tableInfo.getColumnName(field);
			field.setAccessible(true);
			try {
				Object o = processColumn(rs, fieldName, field.getType());
				callSetter(result,field,o);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;

	}

	private final Class<T> type;
	private final TableInfo tableInfo;

	public BeanListMHandler(Class<T> type) {
		super();
		this.type = type;
		this.tableInfo = new TableInfo(type);
	}

	protected Object processColumn(ResultSet rs, String index, Class<?> propType)
			throws SQLException {

		if (!propType.isPrimitive() && rs.getObject(index) == null) {
			return null;
		}

		if (propType.equals(String.class)) {
			return rs.getString(index);

		} else if (propType.equals(Integer.TYPE)
				|| propType.equals(Integer.class)) {
			return Integer.valueOf(rs.getInt(index));

		} else if (propType.equals(Boolean.TYPE)
				|| propType.equals(Boolean.class)) {
			return Boolean.valueOf(rs.getBoolean(index));

		} else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
			return Long.valueOf(rs.getLong(index));

		} else if (propType.equals(Double.TYPE)
				|| propType.equals(Double.class)) {
			return Double.valueOf(rs.getDouble(index));

		} else if (propType.equals(Float.TYPE) || propType.equals(Float.class)) {
			return Float.valueOf(rs.getFloat(index));

		} else if (propType.equals(Short.TYPE) || propType.equals(Short.class)) {
			return Short.valueOf(rs.getShort(index));

		} else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
			return Byte.valueOf(rs.getByte(index));

		} else if (propType.equals(Timestamp.class)) {
			return rs.getTimestamp(index);

		} else if (propType.equals(SQLXML.class)) {
			return rs.getSQLXML(index);

		} else {
			return rs.getObject(index);
		}

	}

	private void callSetter(Object target, Field prop, Object value)
			throws SQLException {


		if (prop == null) {
			return;
		}
        
		Class<?> params =prop.getType();
		try {
			// convert types for some popular ones
			if (value instanceof java.util.Date) {
				final String targetType = params.getName();
				if ("java.sql.Date".equals(targetType)) {
					value = new java.sql.Date(
							((java.util.Date) value).getTime());
				} else if ("java.sql.Time".equals(targetType)) {
					value = new java.sql.Time(
							((java.util.Date) value).getTime());
				} else if ("java.sql.Timestamp".equals(targetType)) {
					value = new java.sql.Timestamp(
							((java.util.Date) value).getTime());
				}
			}

			// Don't call setter if the value object isn't the right type
			if (this.isCompatibleType(value, params)) {
				prop.set(target, value);
			} else {
				throw new SQLException("Cannot set " + prop.getName()
						+ ": incompatible types, cannot convert "
						+ value.getClass().getName() + " to "
						+ params.getName());
				// value cannot be null here because isCompatibleType allows
				// null
			}

		} catch (IllegalArgumentException e) {
			throw new SQLException("Cannot set " + prop.getName() + ": "
					+ e.getMessage());

		} catch (IllegalAccessException e) {
			throw new SQLException("Cannot set " + prop.getName() + ": "
					+ e.getMessage());

		}
	}

	private boolean isCompatibleType(Object value, Class<?> type) {
		// Do object check first, then primitives
		if (value == null || type.isInstance(value)) {
			return true;

		} else if (type.equals(Integer.TYPE) && Integer.class.isInstance(value)) {
			return true;

		} else if (type.equals(Long.TYPE) && Long.class.isInstance(value)) {
			return true;

		} else if (type.equals(Double.TYPE) && Double.class.isInstance(value)) {
			return true;

		} else if (type.equals(Float.TYPE) && Float.class.isInstance(value)) {
			return true;

		} else if (type.equals(Short.TYPE) && Short.class.isInstance(value)) {
			return true;

		} else if (type.equals(Byte.TYPE) && Byte.class.isInstance(value)) {
			return true;

		} else if (type.equals(Character.TYPE)
				&& Character.class.isInstance(value)) {
			return true;

		} else if (type.equals(Boolean.TYPE) && Boolean.class.isInstance(value)) {
			return true;

		}
		return false;

	}

	/**
	 * Factory method that returns a new instance of the given Class. This is
	 * called at the start of the bean creation process and may be overridden to
	 * provide custom behavior like returning a cached bean instance.
	 * 
	 * @param <T>
	 *            The type of object to create
	 * @param c
	 *            The Class to create an object from.
	 * @return A newly created object of the Class.
	 * @throws SQLException
	 *             if creation failed.
	 */
	protected <T> T newInstance(Class<T> c) throws SQLException {
		try {
			return c.newInstance();

		} catch (InstantiationException e) {
			throw new SQLException("Cannot create " + c.getName() + ": "
					+ e.getMessage());

		} catch (IllegalAccessException e) {
			throw new SQLException("Cannot create " + c.getName() + ": "
					+ e.getMessage());
		}
	}
}
