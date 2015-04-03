package hm.orz.chaos114.android.carnavimodoki.pref.entity;

import com.os.operando.garum.annotations.Pref;
import com.os.operando.garum.annotations.PrefKey;
import com.os.operando.garum.models.PrefModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Pref(name = "playing_status")
@Data
@EqualsAndHashCode(callSuper = false)
public class PlayingStatus extends PrefModel {
    public enum Type {
        MUSIC, MOVIE
    }

    @PrefKey
    private Type type;
    @PrefKey
    private String mediaId;
    @PrefKey
    private int number;
    @PrefKey
    private int position;

    public void init() {
        type = null;
        mediaId = null;
        number = 0;
        position = 0;
    }

    public static class TypeSerializer extends com.os.operando.garum.serializers.TypeSerializer<Type, String> {

        @Override
        public Class<Type> getDeserializedType() {
            return Type.class;
        }

        @Override
        public Class<String> getSerializedType() {
            return String.class;
        }

        @Override
        public String serialize(Type data) {
            return data == null ? null : data.name();
        }

        @Override
        public Type deserialize(String data) {
            return data == null ? null : Type.valueOf(data);
        }
    }
}
