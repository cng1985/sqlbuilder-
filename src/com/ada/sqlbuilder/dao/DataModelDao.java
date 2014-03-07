package com.ada.sqlbuilder.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


import com.ada.sqlbuilder.Model;
import com.ada.sqlbuilder.MySqlDialect;
import com.ada.sqlbuilder.TableInfo;
import com.ada.sqlbuilder.builder.DeleteBuilder;
import com.ada.sqlbuilder.builder.SelectBuilder;
import com.ada.sqlbuilder.creator.InsertCreator;
import com.ada.sqlbuilder.creator.SelectCreator;
import com.ada.sqlbuilder.creator.UpdateCreator;
import com.ada.sqlbuilder.dbutils.DbUtils;
import com.ada.sqlbuilder.dbutils.ResultSetHandler;
import com.ada.sqlbuilder.dbutils.handlers.BeanListHandler;
import com.ada.sqlbuilder.dbutils.handlers.ScalarHandler;
import com.ada.sqlbuilder.handel.BeanListMHandler;
import com.ada.sqlbuilder.handel.BeanMHandler;
import com.ada.sqlbuilder.page.Pagination;
import com.ada.sqlbuilder.utils.Finder;

public abstract class DataModelDao<T extends Model> extends DataSourceDao {

	Class<T> daoclass;

	public DataModelDao() {
		this.daoclass = getEntityClass();
	}

	abstract protected Class<T> getEntityClass();

	public List<T> query(String sql) {
		List<T> result = null;
		Connection con = null;
		try {
			con = dataSource.getConnection();
			result = queryRunner.query(con, sql, new BeanListMHandler<T>(
					getEntityClass()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(con);
		}
		return result;
	}

	public Pagination<T> page(Finder finder, int pageNo, int pageSize) {
		Pagination<T> result = null;
		Connection con = null;
		try {
			con = dataSource.getConnection();

			int totalCount = countQueryResult(finder);
			result = new Pagination<T>(pageNo, pageSize, totalCount);
			if (totalCount < 1) {
				result.setList(new ArrayList<T>());
				return result;
			}

			List<T> results = queryRunner.query(con, finder.getOrigHql(),
					new BeanListHandler<T>(daoclass));
			result.setList(results);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(con);
		}
		return result;
	}

	public Pagination<T> pageBySelect(SelectCreator creator, int pageNo,
			int pageSize) {
		Pagination<T> result = pageBySelect(creator, pageNo, pageSize,
				new BeanListMHandler<T>(getEntityClass()));
		return result;
	}

	public Pagination<T> pageBySelect(SelectCreator creator, int pageNo,
			int pageSize, ResultSetHandler<List<T>> handler) {
		Pagination<T> result = null;
		Connection con = null;
		try {
			con = dataSource.getConnection();
			PreparedStatement stmts = creator.count(new MySqlDialect())
					.createPreparedStatement(con);
			long totalCounts = queryRunner.query(stmts,
					new ScalarHandler<Long>());
			int totalCount = Integer.valueOf(totalCounts + "");
			result = new Pagination<T>(pageNo, pageSize, totalCount);
			if (totalCount < 1) {
				result.setList(new ArrayList<T>());
				return result;
			}
			PreparedStatement stmt = creator.page(new MySqlDialect(),
					result.getLimitSize(), result.getPageSize())
					.createPreparedStatement(con);
			List<T> results = queryRunner.query(stmt, handler);
			result.setList(results);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(con);
		}
		return result;
	}

	private int countQueryResult(Finder finder) {
		int result = 0;
		Connection con = null;
		try {
			con = dataSource.getConnection();
			Long l = queryRunner.query(con, finder.getRowCountHql(),
					new ScalarHandler<Long>());
			result = Integer.valueOf(l.toString());
		} catch (Exception e) {

		}
		return result;
	}

	public T findById(long id) {
		T result = null;
		Connection con = null;
		try {
			con = dataSource.getConnection();
			TableInfo mTableInfo = new TableInfo(daoclass);
			SelectBuilder builder = new SelectBuilder(mTableInfo.getTableName());
			builder.where("id=" + id);
			result = queryRunner.query(con, builder.toString(),
					new BeanMHandler<T>(getEntityClass()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(con);
		}
		return result;
	}

	public int delete(long id) {
		int result = -1;
		TableInfo mTableInfo = new TableInfo(daoclass);
		DeleteBuilder b = new DeleteBuilder(mTableInfo.getTableName());
		b.set("id =" + id);
		result = delete(b);
		return result;
	}

	public int delete(DeleteBuilder b) {
		int result = -1;
		Connection con = null;
		try {
			con = dataSource.getConnection();
			result = queryRunner.update(con, b.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(con);
		}
		return result;
	}

	public Long add(T entity) {
		Long result = null;
		Connection con = null;
		try {
			con = dataSource.getConnection();
			TableInfo mTableInfo = new TableInfo(daoclass);
			InsertCreator creator = new InsertCreator(mTableInfo.getTableName());
			for (Field field : mTableInfo.getFields()) {
				final String fieldName = mTableInfo.getColumnName(field);
				if (!fieldName.equals("id")) {
					field.setAccessible(true);
					Object value = field.get(entity);
					creator.setValue(fieldName, value);
				}
			}
			PreparedStatement p = creator.createPreparedStatement(con);
			p.executeUpdate();
			result = queryRunner.query(con, "SELECT last_insert_id()",
					new ScalarHandler<Long>());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(con);
		}
		return result;
	}

	public T update(T entity) {
		T result = null;
		Connection con = null;
		try {
			con = dataSource.getConnection();
			TableInfo mTableInfo = new TableInfo(daoclass);
			UpdateCreator creator = new UpdateCreator(mTableInfo.getTableName());
			for (Field field : mTableInfo.getFields()) {
				final String fieldName = mTableInfo.getColumnName(field);
				if (!fieldName.equals("id")) {
					field.setAccessible(true);
					Object value = field.get(entity);
					creator.setValue(fieldName, value);
				} else {
					field.setAccessible(true);
					Object value = field.get(entity);
					creator.whereEquals(fieldName, value);
				}
			}
			PreparedStatement p = creator.createPreparedStatement(con);
			p.executeUpdate();
			result = entity;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(con);
		}
		return result;
	}
}
