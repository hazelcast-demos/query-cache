package org.hazelcast.query.client;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.serialization.GenericRecord;
import com.hazelcast.query.Predicates;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

@ShellComponent
public class QueryCommand {

    private static final ModelEnricher[] ENRICHERS = new ModelEnricher[]{
            new ModelEnricher("component", record -> record.getString("component")),
            new ModelEnricher("type", record -> record.getString("type")),
            new ModelEnricher("x", record -> record.getInt("x")),
            new ModelEnricher("y", record -> record.getInt("y")),
            new ModelEnricher("event", record -> record.getString("event")),
            new ModelEnricher("timestamp", record -> record.getLong("timestamp")),
            new ModelEnricher("instant", record -> record.getLong("instant")),
            new ModelEnricher("session", record -> record.getString("session"))
    };

    private final HazelcastInstance client;

    public QueryCommand(HazelcastInstance client) {
        this.client = client;
    }

    @ShellMethod(
            key = { "query-component", "query", "q" },
            value = "Query the Hazelcast analytics map by component's name"
    )
    public Table queryByComponentName(
            @ShellOption(
                    help = "The name of the component. For the demo, valid values are first-name, last-name, range, success-button and first-button"
            ) String name
    ) {
        var modelBuilder = new TableModelBuilder<>();
        var map = client.getMap("analytics");
        var entry = Predicates.newPredicateBuilder().getEntryObject();
        var predicate = entry.get("component").equal(name);
        Collection<GenericRecord> values = map.values(predicate);
        modelBuilder.addRow();
        Stream.of(ENRICHERS)
                .map(ModelEnricher::getFieldName)
                .forEach(modelBuilder::addValue);
        values.stream()
                .forEach(record -> {
                    modelBuilder.addRow();
                    Stream.of(ENRICHERS).forEach(enricher -> enricher.accept(modelBuilder, record));
                });
        var builder = new TableBuilder(modelBuilder.build());
        builder.addFullBorder(BorderStyle.oldschool);
        return builder.build();
    }

    private static class ModelEnricher implements BiConsumer<TableModelBuilder<Object>, GenericRecord> {

        private final String fieldName;
        private final Function<GenericRecord, Object> extractor;

        private ModelEnricher(String fieldName, Function<GenericRecord, Object> extractor) {
            this.fieldName = fieldName;
            this.extractor = extractor;
        }

        public String getFieldName() {
            return fieldName;
        }

        @Override
        public void accept(TableModelBuilder<Object> modelBuilder, GenericRecord record) {
            if (record.hasField(fieldName)) {
                modelBuilder.addValue(extractor.apply(record));
            } else {
                modelBuilder.addValue(null);
            }
        }
    }
}
