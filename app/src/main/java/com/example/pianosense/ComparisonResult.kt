package com.example.pianosense;

public class ComparisonResult {
    private final NoteInfo originalNote;
    private final NoteInfo recordedNote;
    private final boolean isCorrect;

    public ComparisonResult(NoteInfo originalNote, NoteInfo recordedNote, boolean isCorrect) {
        this.originalNote = originalNote;
        this.recordedNote = recordedNote;
        this.isCorrect = isCorrect;
    }

    public NoteInfo getOriginalNote() {
        return originalNote;
    }

    public NoteInfo getRecordedNote() {
        return recordedNote;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    @Override
    public String toString() {
        return "ComparisonResult{" +
                "originalNote=" + originalNote +
                ", recordedNote=" + recordedNote +
                ", isCorrect=" + isCorrect +
                '}';
    }
}
