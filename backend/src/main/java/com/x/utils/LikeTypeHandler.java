package com.x.utils;

import com.x.common.enmu.LikeType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeTypeHandler extends BaseTypeHandler<LikeType> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LikeType parameter, JdbcType jdbcType) throws SQLException {
        ps.setByte(i, parameter.getCode()); // 枚举转数据库数字
    }

    @Override
    public LikeType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte code = rs.getByte(columnName);
        return LikeType.fromCode(code); // 数据库数字转枚举
    }

    @Override
    public LikeType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte code = rs.getByte(columnIndex);
        return LikeType.fromCode(code);
    }

    @Override
    public LikeType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte code = cs.getByte(columnIndex);
        return LikeType.fromCode(code);
    }
}
