package io.shanepark.github.postgresarrayjpa;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Objects;

public class StringArrayType implements UserType<String[]> {

    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @Override
    public Class<String[]> returnedClass() {
        return String[].class;
    }

    @Override
    public boolean equals(String[] x, String[] y) {
        return Objects.deepEquals(x, y);
    }

    @Override
    public int hashCode(String[] x) {
        return x.hashCode();
    }

    @Override
    public String[] nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        Array array = rs.getArray(position);
        if (array == null) {
            return null;
        }
        return (String[]) array.getArray();
    }

    @Override
    public void nullSafeSet(PreparedStatement st, String[] value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.ARRAY);
            return;
        }
        Array array = session.getJdbcConnectionAccess()
                .obtainConnection()
                .createArrayOf("text", value);
        st.setArray(index, array);
    }

    @Override
    public String[] deepCopy(String[] value) {
        return value != null ? value.clone() : null;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(String[] value) {
        return deepCopy(value);
    }

    @Override
    public String[] assemble(Serializable cached, Object owner) {
        return deepCopy((String[]) cached);
    }
}
