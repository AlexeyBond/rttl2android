package org.iplusplus.rttf2android.composition;

import junit.framework.TestCase;

public class NoteTest extends TestCase {

    public void testFromString() throws Exception {
        Note note = Note.fromString("16C2",Note.defaultDefaults);

        assertNotNull(note);
        assertEquals(note.getOffsetFrom(Note.Offsets.C, 1), 12);

        note = Note.fromString("",Note.defaultDefaults);

        assertNull(note);
    }

    public void testToString() throws Exception {

    }
}
