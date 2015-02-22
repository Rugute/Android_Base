package ca.dalezak.android.base.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

import ca.dalezak.android.base.utils.Log;

import java.lang.reflect.Field;
import java.util.List;

public abstract class Model extends com.activeandroid.Model {

    public static boolean hasColumns(Class<? extends Model> model, Class<? extends Columns> fields) {
        StringBuilder clause = new StringBuilder();
        for (Field field : fields.getFields()) {
            try {
                if (clause.length() > 0) {
                    clause.append(" AND ");
                }
                clause.append(field.get(null).toString());
                clause.append(" IS NOT NULL");
            }
            catch (Exception e) {
                Log.i(Model.class, "Exception", e);
            }
        }
        try {
            new Select().
                    from(model).
                    where(clause.toString()).
                    count();
            return true;
        }
        catch (Exception e) {
            Log.i(Model.class, "Exception", e);
        }
        return false;
    }

    public class Columns {
        public static final String UUID = "uuid";
    }

    @Column(name = Columns.UUID, unique = true, index = true)
    public String uuid;

    public static <M extends Model> Integer count(Class<M> type) {
        return new Select().
                from(type).
                count();
    }

    public static <M extends Model> Integer count(Class<M> type, String clause, Object...args) {
        return new Select().
                from(type).
                where(clause, args).
                count();
    }

    public static <M extends Model> List<M> all(Class<M> type) {
        return new Select().
                from(type).
                execute();
    }

    public static <M extends Model> List<M> all(Class<M> type, String orderBy, Boolean asc) {
        return new Select().
                from(type).
                orderBy(String.format("%s %s", orderBy, asc ? "ASC" : "DESC")).
                execute();
    }

    public static <M extends Model> List<M> all(Class<M> type, Model parent, String orderBy, Boolean asc) {
        return new Select().
                from(type).
                where(String.format("%s = ?", parent.getClass().getSimpleName()), parent.getId()).
                orderBy(String.format("%s %s", orderBy, asc ? "ASC" : "DESC")).
                execute();
    }

    public static <M extends Model> List<M> where(Class<M> type, String clause, Object...args) {
        return new Select().
                from(type).
                where(clause, args).
                execute();
    }

    public static <M extends Model> List<M> where(Class<M> type, String orderBy, Boolean asc, String clause, Object...args) {
        return new Select().
                from(type).
                where(clause, args).
                orderBy(String.format("%s %s", orderBy, asc ? "ASC" : "DESC"))
                .execute();
    }

    public static <M extends Model> M find(Class<M> type, String identifier) {
        if (identifier != null) {
            return new Select().from(type).where(String.format("%s = ?", Columns.UUID), identifier).executeSingle();
        }
        return null;
    }

    protected static <M extends Model> M find(Class<M> type, String clause, String args) {
        return new Select().
                from(type).
                where(clause, args).
                executeSingle();
    }

    public abstract boolean matches(String text);
}