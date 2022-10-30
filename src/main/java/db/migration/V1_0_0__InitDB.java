package db.migration;

import java.sql.PreparedStatement;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V1_0_0__InitDB extends BaseJavaMigration {

    private static final String CREATE_TABLE_PUBLIC_GAMEINFO = "" +
            "CREATE TABLE public.gameinfo("
            + "id varchar(128), "
            + "symbol varchar(1), "
            + "x int, "
            + "y int, "
            + "CONSTRAINT unique_entry UNIQUE (id, x, y), "
            + "CONSTRAINT chk_symbol CHECK (symbol IN ('X', 'O'))" +
            ");";

    @Override
    public void migrate(Context context) throws Exception {
        try (PreparedStatement statement = context.getConnection()
                .prepareStatement(CREATE_TABLE_PUBLIC_GAMEINFO)) {
            statement.execute();
        }
    }
}
