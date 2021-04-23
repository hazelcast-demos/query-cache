package org.hazelcast.query.client;

import java.sql.DriverManager;
import java.sql.SQLException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

@ShellComponent
public class JdbcCommand {

    @ShellMethod(
            key = {"jdbc-component-range", "jdbc-comp-range", "jcr"},
            value = "Query the Hazelcast analytics map by component's name and below a instant with JDBC"
    )
    public Table jdbcByComponentNameAndWithinTimeRange(
            @ShellOption(
                    help = "The name of the component. For the demo, valid values are first-name, last-name, range, success-button and first-button"
            ) String name,
            @ShellOption(
                    help = "The instant max value. For the demo, instant ranges from 0 to 99,999"
            ) long instant
    ) throws SQLException {
        try (var connection = DriverManager.getConnection("jdbc:hazelcast://localhost:5701/");
             var statement = connection.prepareStatement("SELECT * FROM analytics where component = ? and instant < ?")) {
            statement.setString(1, name);
            statement.setLong(2, instant);
            var resultSet = statement.executeQuery();
            var modelBuilder = new TableModelBuilder<>();
            modelBuilder.addRow();
            modelBuilder.addValue("component");
            modelBuilder.addValue("type");
            modelBuilder.addValue("x");
            modelBuilder.addValue("y");
            modelBuilder.addValue("event");
            modelBuilder.addValue("timestamp");
            modelBuilder.addValue("instant");
            modelBuilder.addValue("session");
            while (resultSet.next()) {
                modelBuilder.addRow();
                var component = resultSet.getString("component");
                modelBuilder.addValue(component);
                var type = resultSet.getString("type");
                modelBuilder.addValue(type);
                var x = resultSet.getInt("x");
                modelBuilder.addValue(x);
                var y = resultSet.getInt("y");
                modelBuilder.addValue(y);
                var event = resultSet.getString("event");
                modelBuilder.addValue(event);
                var timestamp = resultSet.getLong("timestamp");
                modelBuilder.addValue(timestamp);
                var theInstant = resultSet.getLong("instant");
                modelBuilder.addValue(theInstant);
                var session = resultSet.getString("session");
                modelBuilder.addValue(session);
            }
            var builder = new TableBuilder(modelBuilder.build());
            builder.addFullBorder(BorderStyle.oldschool);
            return builder.build();
        }
    }
}
