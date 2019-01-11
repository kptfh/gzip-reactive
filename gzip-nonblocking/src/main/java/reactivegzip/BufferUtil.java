package reactivegzip;

import java.nio.ByteBuffer;

public class BufferUtil {

    public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);

    public static boolean isEmpty(ByteBuffer buf) {
        return buf == null || buf.remaining() == 0;
    }

    public static int append(ByteBuffer to, ByteBuffer b) {
        int pos = flipToFill(to);

        int var3;
        try {
            var3 = put(b, to);
        } finally {
            flipToFlush(to, pos);
        }

        return var3;
    }

    public static int put(ByteBuffer from, ByteBuffer to) {
        int remaining = from.remaining();
        int put;
        if (remaining > 0) {
            if (remaining <= to.remaining()) {
                to.put(from);
                put = remaining;
                from.position(from.limit());
            } else if (from.hasArray()) {
                put = to.remaining();
                to.put(from.array(), from.arrayOffset() + from.position(), put);
                from.position(from.position() + put);
            } else {
                put = to.remaining();
                ByteBuffer slice = from.slice();
                slice.limit(put);
                to.put(slice);
                from.position(from.position() + put);
            }
        } else {
            put = 0;
        }

        return put;
    }

    public static void flipToFlush(ByteBuffer buffer, int position) {
        buffer.limit(buffer.position());
        buffer.position(position);
    }

    public static int flipToFill(ByteBuffer buffer) {
        int position = buffer.position();
        int limit = buffer.limit();
        if (position == limit) {
            buffer.position(0);
            buffer.limit(buffer.capacity());
            return 0;
        } else {
            int capacity = buffer.capacity();
            if (limit == capacity) {
                buffer.compact();
                return 0;
            } else {
                buffer.position(limit);
                buffer.limit(capacity);
                return position;
            }
        }
    }



}
