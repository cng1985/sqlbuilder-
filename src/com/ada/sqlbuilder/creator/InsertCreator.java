package com.ada.sqlbuilder.creator;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ada.sqlbuilder.builder.InsertBuilder;



public class InsertCreator implements   PreparedStatementCreator, Serializable {

    private static final long serialVersionUID = 1;

    private InsertBuilder builder;

    private ParameterizedPreparedStatmentCreator ppsc = new ParameterizedPreparedStatmentCreator();

    public InsertCreator(String table) {
        builder = new InsertBuilder(table);
    }

    public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
        ppsc.setSql(builder.toString());
        return ppsc.createPreparedStatement(conn);
    }

    public ParameterizedPreparedStatmentCreator setParameter(String name, Object value) {
        return ppsc.setParams(name, value);
    }

    public InsertCreator setRaw(String column, String value) {
        builder.set(column, value);
        return this;
    }

    public InsertCreator setValue(String column, Object value) {
        setRaw(column, ":" + column);
        setParameter(column, value);
        return this;
    }

}