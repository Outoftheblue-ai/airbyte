/*
 * MIT License
 *
 * Copyright (c) 2020 Airbyte
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.airbyte.integrations.source.druid;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.airbyte.db.SourceOperations;
import io.airbyte.db.jdbc.JdbcSourceOperations;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.JDBCType;
import java.sql.ResultSet;

public class DruidSourceOperations extends JdbcSourceOperations implements SourceOperations<ResultSet, JDBCType> {

  public DruidSourceOperations() {
    super();
  }

  // We need to override this method to handle __time comumn from druid. Druid stores the time
  // column with the type value '1111' which is a implementation specific type. The standard method defined
  // in AbstractJdbcSource sets the type as JDBCType::OTHER which is not understood by the later
  // code. We force the type of this column to JDBCType:BIGINT, since airbyte sees is pretty much like
  // BIGINT. 
  public void setStatementField(PreparedStatement preparedStatement,
                                int parameterIndex,
                                JDBCType cursorFieldType,
                                String value)
      throws SQLException {
    switch (cursorFieldType) {
      case OTHER -> setBigInteger(preparedStatement, parameterIndex, value);
      default -> super.setStatementField(preparedStatement, parameterIndex, cursorFieldType, value);
    }
  }
}
