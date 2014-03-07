package com.ada.sqlbuilder.dao;

import java.io.IOException;
import java.util.Map;

import javax.sql.DataSource;


import com.ada.sqlbuilder.dbutils.QueryLoader;
import com.ada.sqlbuilder.dbutils.QueryRunner;

public class DataSourceDao {
	protected DataSource dataSource;
	protected QueryRunner queryRunner = new QueryRunner();

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	protected Map<String, String> sqls;

	public DataSourceDao() {
		super();
	}
	protected void loadSql() {
		try {
			sqls = QueryLoader.instance().load(
					getpath() + getClass().getSimpleName()
							+ ".properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String getpath() {
		return "/com/baemusic/dao/";
	}
	
	
}
