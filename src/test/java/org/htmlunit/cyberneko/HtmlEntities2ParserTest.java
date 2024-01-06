package org.htmlunit.cyberneko;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.htmlunit.benchmark.util.FastRandom;
import org.htmlunit.benchmark.util.RandomUtils;
import org.htmlunit.cyberneko.util.HtmlEntities2;
import org.htmlunit.cyberneko.util.HtmlEntities2.Level;
import org.junit.jupiter.api.Test;

public class HtmlEntities2ParserTest
{
    @Test
    public void happyPath()
    {
        final Optional<Level> r = HtmlEntities2.get().lookup("Beta;");
        assertTrue(r.get().isMatch);
        assertTrue(r.get().endNode);
        assertTrue(r.get().endsWithSemicolon);

        assertEquals("\u0392", r.get().resolvedValue);
        assertEquals("Beta;", r.get().entityOrFragment);
        assertEquals(5, r.get().length);
    }

    @Test
    public void happyPathOneCharDiff()
    {
        {
            final Optional<Level> r = HtmlEntities2.get().lookup("Colon;");
            assertTrue(r.get().isMatch);
            assertTrue(r.get().endNode);
            assertTrue(r.get().endsWithSemicolon);

            assertEquals("\u2237", r.get().resolvedValue);
            assertEquals("Colon;", r.get().entityOrFragment);
            assertEquals(6, r.get().length);
        }
        {
            final Optional<Level> r = HtmlEntities2.get().lookup("Colone;");
            assertTrue(r.get().isMatch);
            assertTrue(r.get().endNode);
            assertTrue(r.get().endsWithSemicolon);

            assertEquals("\u2A74", r.get().resolvedValue);
            assertEquals("Colone;", r.get().entityOrFragment);
            assertEquals(7, r.get().length);
        }
    }

    @Test
    public void happyPathTwoVersionEntity()
    {
        {
            final Optional<Level> r = HtmlEntities2.get().lookup("gt");
            assertEquals("gt", r.get().entityOrFragment);
            assertTrue(r.get().isMatch);
            assertFalse(r.get().endNode);
            assertFalse(r.get().endsWithSemicolon);

            assertEquals(">", r.get().resolvedValue);
            assertEquals(2, r.get().length);
        }
        {
            final Optional<Level> r = HtmlEntities2.get().lookup("gt;");
            assertEquals("gt;", r.get().entityOrFragment);
            assertTrue(r.get().isMatch);
            assertTrue(r.get().endNode);
            assertTrue(r.get().endsWithSemicolon);

            assertEquals(">", r.get().resolvedValue);
            assertEquals(3, r.get().length);
        }
    }

    @Test
    public void happyPathTwoVersionEntity2()
    {
        {
            final Optional<Level> r = HtmlEntities2.get().lookup("ccedil");
            assertEquals("ccedil", r.get().entityOrFragment);
            assertTrue(r.get().isMatch);
            assertFalse(r.get().endNode);
            assertFalse(r.get().endsWithSemicolon);

            assertEquals("\u00E7", r.get().resolvedValue);
            assertEquals(6, r.get().length);
        }
        {
            final Optional<Level> r = HtmlEntities2.get().lookup("ccedil;");
            assertEquals("ccedil;", r.get().entityOrFragment);
            assertTrue(r.get().isMatch);
            assertTrue(r.get().endNode);
            assertTrue(r.get().endsWithSemicolon);

            assertEquals("\u00E7", r.get().resolvedValue);
            assertEquals(7, r.get().length);
        }
    }

    @Test
    public void fullyUnknown()
    {
        {
            final Optional<Level> r = HtmlEntities2.get().lookup("abc;");
            assertFalse(r.get().isMatch);
            assertFalse(r.get().endNode);
            assertFalse(r.get().endsWithSemicolon);

            assertEquals(null, r.get().resolvedValue);
            assertEquals("ab", r.get().entityOrFragment);
            assertEquals(2, r.get().length);
        }
    }


    /**
     * Test all entities
     * @throws IOException
     */
    @Test
    public void allEntitiesWithSemicolonFull() throws IOException {
        final Properties props = new Properties();
        try (InputStream stream = HtmlEntities2ParserTest.class.getResourceAsStream("html_entities.properties")) {
            props.load(stream);
        }

        props.forEach((k, v) -> {
            String key = (String) k;
            String value = (String) v;

            // we might have an empty line in it
            // we also don't want to test "old" entities at the moment aka no ; at the end
            if (key.trim().isEmpty()) {
                return;
            }

            final Optional<Level> r = HtmlEntities2.get().lookup(key);
            assertTrue(r.get().isMatch);
            if (key.endsWith(";"))
            {
                assertTrue(r.get().endNode);
                assertTrue(r.get().endsWithSemicolon);
            }
            else
            {
                // no ; means it is never and end node, because this
                // is for legacy entities
                assertFalse(r.get().endNode);
                assertFalse(r.get().endsWithSemicolon);
            }

            assertEquals(value, r.get().resolvedValue);
            assertEquals(key, r.get().entityOrFragment);
            assertEquals(key.length(), r.get().length);
        });
    }

    /**
     * Test all entities
     * @throws IOException
     */
    @Test
    public void allEntitiesFullRandom() throws IOException {
        final Properties props = new Properties();
        try (InputStream stream = HtmlEntities2ParserTest.class.getResourceAsStream("html_entities.properties")) {
            props.load(stream);
        }

        final List<String> keys = new ArrayList<>();
        final List<String> values = new ArrayList<>();

        props.forEach((k, v) -> {
            String key = (String) k;
            String value = (String) v;

            // we might have an empty line in it
            if (key.isEmpty()) {
                return;
            }

            // we need randomness to avoid that the setup data looks identical to the quueried data
            FastRandom r = new FastRandom();
            int pos = r.nextInt(keys.size() + 1);

            keys.add(pos, key);
            values.add(pos, value);
        });


        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = values.get(i);

            // we might have an empty line in it
            // we also don't want to test "old" entities at the moment aka no ; at the end
            if (key.trim().isEmpty()) {
                return;
            }

            final Optional<Level> r = HtmlEntities2.get().lookup(key);
            assertTrue(r.get().isMatch);
            if (key.endsWith(";"))
            {
                assertTrue(r.get().endNode);
                assertTrue(r.get().endsWithSemicolon);
            }
            else
            {
                // no ; means it is never and end node, because this
                // is for legacy entities
                assertFalse(r.get().endNode);
                assertFalse(r.get().endsWithSemicolon);
            }

            assertEquals(value, r.get().resolvedValue);
            assertEquals(key, r.get().entityOrFragment);
            assertEquals(key.length(), r.get().length);
        }
    }
}
