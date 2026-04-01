package com.hotel.view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public final class ViewSupport {

    private ViewSupport() {
    }

    public static DefaultTableModel createReadOnlyTableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static DocumentListener simpleDocumentListener(Runnable action) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        };
    }

    public static void installSearchFilter(JTextField searchField, TableRowSorter<DefaultTableModel> sorter) {
        searchField.getDocument().addDocumentListener(simpleDocumentListener(() ->
                applySearchFilter(sorter, searchField.getText())));
    }

    public static void applySearchFilter(TableRowSorter<DefaultTableModel> sorter, String query) {
        if (sorter == null) {
            return;
        }
        String normalized = query == null ? "" : query.trim();
        if (normalized.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(normalized)));
    }

    public static JTextField createSearchField(String tooltip) {
        JTextField field = new JTextField(16);
        UiStyles.styleTextField(field);
        field.setToolTipText(tooltip);
        return field;
    }

    public static void installIsoDateInput(JTextField field, String tooltip) {
        field.setToolTipText(tooltip);
        if (field.getDocument() instanceof AbstractDocument document) {
            document.setDocumentFilter(new IsoDateDocumentFilter());
        }
    }

    public static <T> void runAsync(
            Component owner,
            Runnable before,
            Supplier<T> task,
            Consumer<T> onSuccess,
            Consumer<Exception> onError
    ) {
        if (before != null) {
            before.run();
        }

        Component cursorTarget = owner == null ? null : SwingUtilities.getWindowAncestor(owner);
        if (cursorTarget == null) {
            cursorTarget = owner;
        }
        if (cursorTarget != null) {
            cursorTarget.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }

        Component finalCursorTarget = cursorTarget;
        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() {
                return task.get();
            }

            @Override
            protected void done() {
                if (finalCursorTarget != null) {
                    finalCursorTarget.setCursor(Cursor.getDefaultCursor());
                }
                try {
                    T result = get();
                    if (onSuccess != null) {
                        onSuccess.accept(result);
                    }
                } catch (Exception ex) {
                    Exception businessException = ex instanceof java.util.concurrent.ExecutionException executionException
                            && executionException.getCause() instanceof Exception cause
                            ? cause
                            : ex;
                    if (onError != null) {
                        onError.accept(businessException);
                    }
                }
            }
        };
        worker.execute();
    }

    private static final class IsoDateDocumentFilter extends DocumentFilter {
        private static final Pattern PARTIAL_DATE_PATTERN = Pattern.compile("^\\d{0,4}(-\\d{0,2}){0,2}$");

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            replace(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String replacement = text == null ? "" : text;
            String candidate = current.substring(0, offset) + replacement + current.substring(offset + length);
            if (candidate.length() <= 10 && PARTIAL_DATE_PATTERN.matcher(candidate).matches()) {
                super.replace(fb, offset, length, replacement, attrs);
            }
        }
    }
}
