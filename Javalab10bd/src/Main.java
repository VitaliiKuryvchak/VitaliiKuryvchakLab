import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    private JComboBox<String> tableComboBox;
    private JTable dataTable;
    private JButton refreshBtn, createBtn, updateBtn, deleteBtn;
    private JScrollPane tableScrollPane;
    private JTextField searchField;
    private JComboBox<String> searchColumnCombo;
    private TableRowSorter<TableModel> sorter;

    // Параметри підключення до SQL Server
    private final String DB_URL = "jdbc:sqlserver://DESKTOP-IL03BR7\\SQLEXPRESS01:1433;" +
            "databaseName=LAB3;" +
            "integratedSecurity=true;" +
            "trustServerCertificate=true";

    public Main() {
        super("SQL Server — CRUD Manager");
        setLayout(new BorderLayout());
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Панель керування
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        tableComboBox = new JComboBox<>();
        refreshBtn = new JButton("🔄 Оновити");
        createBtn = new JButton("➕ Додати");
        updateBtn = new JButton("✏️ Редагувати");
        deleteBtn = new JButton("❌ Видалити");

        controlPanel.add(new JLabel("Оберіть таблицю:"));
        controlPanel.add(tableComboBox);
        controlPanel.add(refreshBtn);
        controlPanel.add(createBtn);
        controlPanel.add(updateBtn);
        controlPanel.add(deleteBtn);

        // Додаємо елементи для пошуку
        controlPanel.add(new JLabel("Пошук:"));
        searchField = new JTextField(15);
        controlPanel.add(searchField);

        controlPanel.add(new JLabel("в стовпці:"));
        searchColumnCombo = new JComboBox<>();
        searchColumnCombo.addItem("Всі стовпці");
        searchColumnCombo.setPreferredSize(new Dimension(120, searchField.getPreferredSize().height));
        controlPanel.add(searchColumnCombo);

        // Таблиця для відображення даних
        dataTable = new JTable();
        // Увімкнення сортування при кліку на заголовки стовпців
        dataTable.setAutoCreateRowSorter(true);
        tableScrollPane = new JScrollPane(dataTable);

        add(controlPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Обробники подій
        refreshBtn.addActionListener(e -> refreshTableData());
        tableComboBox.addActionListener(e -> refreshTableData());
        createBtn.addActionListener(e -> showCreateDialog());
        updateBtn.addActionListener(e -> showUpdateDialog());
        deleteBtn.addActionListener(e -> deleteSelectedRecord());

        // Обробник пошуку
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search();
            }
        });

        searchColumnCombo.addActionListener(e -> search());

        // Завантажити список таблиць при запуску
        loadTableNames();

        setVisible(true);
    }

    private void search() {
        if (sorter == null) return;

        String text = searchField.getText().trim();
        String selectedColumn = (String) searchColumnCombo.getSelectedItem();

        if (text.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        try {
            // Фільтр для всіх стовпців
            if ("Всі стовпці".equals(selectedColumn)) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
            // Фільтр для конкретного стовпця
            else {
                int columnIndex = searchColumnCombo.getSelectedIndex() - 1;
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, columnIndex));
            }
        } catch (Exception e) {
            // Ігноруємо помилки в регулярних виразах
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void loadTableNames() {
        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            tableComboBox.removeAllItems();
            while (tables.next()) {
                tableComboBox.addItem(tables.getString("TABLE_NAME"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка завантаження таблиць: " + ex.getMessage(),
                    "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTableData() {
        String tableName = (String) tableComboBox.getSelectedItem();
        if (tableName == null) return;

        try (Connection conn = getConnection()) {
            // Отримати структуру таблиці
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);

            List<String> columnNames = new ArrayList<>();
            List<Class<?>> columnClasses = new ArrayList<>(); // Для зберігання класів стовпців

            while (columns.next()) {
                columnNames.add(columns.getString("COLUMN_NAME"));
                String typeName = columns.getString("TYPE_NAME");
                columnClasses.add(getJavaClass(typeName)); // Визначаємо Java-клас для стовпця
            }

            // Оновити комбобокс стовпців для пошуку
            searchColumnCombo.removeAllItems();
            searchColumnCombo.addItem("Всі стовпці");
            for (String colName : columnNames) {
                searchColumnCombo.addItem(colName);
            }

            // Отримати дані таблиці
            String sql = "SELECT * FROM " + tableName;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // Створити модель таблиці з підтримкою типів даних
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnClasses.get(columnIndex);
                }
            };

            // Додати назви стовпців
            for (String colName : columnNames) {
                model.addColumn(colName);
            }

            // Додати дані
            while (rs.next()) {
                Object[] rowData = new Object[columnNames.size()];
                for (int i = 0; i < columnNames.size(); i++) {
                    rowData[i] = rs.getObject(columnNames.get(i));
                }
                model.addRow(rowData);
            }

            dataTable.setModel(model);

            // Ініціалізуємо сортувальник
            sorter = new TableRowSorter<>(dataTable.getModel());
            dataTable.setRowSorter(sorter);

            // Застосувати поточний фільтр пошуку
            search();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка завантаження даних: " + ex.getMessage(),
                    "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Метод для визначення Java-класу на основі типу даних SQL
    private Class<?> getJavaClass(String sqlType) {
        if (sqlType == null || sqlType.trim().isEmpty()) {
            return Object.class;
        }

        String typeUpper = sqlType.toUpperCase();
        switch (typeUpper) {
            case "INT":
            case "INTEGER":
                return Integer.class;
            case "BIGINT":
                return Long.class;
            case "SMALLINT":
            case "TINYINT":
                return Integer.class;
            case "REAL":
                return Float.class;
            case "FLOAT":
            case "DOUBLE":
                return Double.class;
            case "DECIMAL":
            case "NUMERIC":
            case "MONEY":
            case "SMALLMONEY":
                return java.math.BigDecimal.class;
            case "BIT":
                return Boolean.class;
            case "DATE":
                return java.sql.Date.class;
            case "TIME":
                return java.sql.Time.class;
            case "DATETIME":
            case "DATETIME2":
            case "SMALLDATETIME":
            case "TIMESTAMP":
                return java.sql.Timestamp.class;
            case "CHAR":
            case "VARCHAR":
            case "LONGVARCHAR":
            case "NCHAR":
            case "NVARCHAR":
            case "LONGNVARCHAR":
            case "TEXT":
            case "NTEXT":
                return String.class;
            default:
                return String.class; // Тип за замовчуванням
        }
    }

    private void showCreateDialog() {
        String tableName = (String) tableComboBox.getSelectedItem();
        if (tableName == null) return;

        try (Connection conn = getConnection()) {
            // Отримати інформацію про стовпці
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);

            List<String> columnNames = new ArrayList<>();
            List<String> columnTypes = new ArrayList<>();
            List<Boolean> isAutoIncrement = new ArrayList<>();

            while (columns.next()) {
                columnNames.add(columns.getString("COLUMN_NAME"));
                columnTypes.add(columns.getString("TYPE_NAME"));
                isAutoIncrement.add("YES".equals(columns.getString("IS_AUTOINCREMENT")));
            }

            // Створити діалогове вікно
            JPanel panel = new JPanel(new GridLayout(columnNames.size(), 2, 5, 5));
            List<JTextField> inputs = new ArrayList<>();

            for (int i = 0; i < columnNames.size(); i++) {
                if (isAutoIncrement.get(i)) continue; // Пропустити автоінкрементні поля

                panel.add(new JLabel(columnNames.get(i) + " (" + columnTypes.get(i) + "):"));
                JTextField textField = new JTextField(20);
                panel.add(textField);
                inputs.add(textField);
            }

            int result = JOptionPane.showConfirmDialog(
                    this, panel, "Додати запис до " + tableName,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                // Побудувати SQL запит
                StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
                StringBuilder values = new StringBuilder("VALUES (");

                int inputIndex = 0;
                for (int i = 0; i < columnNames.size(); i++) {
                    if (isAutoIncrement.get(i)) continue;

                    if (inputIndex > 0) {
                        sql.append(", ");
                        values.append(", ");
                    }

                    sql.append(columnNames.get(i));
                    values.append("?");

                    inputIndex++;
                }

                sql.append(") ").append(values).append(")");

                // Виконати запит
                try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                    inputIndex = 0;
                    for (int i = 0; i < columnNames.size(); i++) {
                        if (isAutoIncrement.get(i)) continue;

                        String value = inputs.get(inputIndex).getText();
                        setParameter(stmt, inputIndex + 1, value, columnTypes.get(i));
                        inputIndex++;
                    }

                    stmt.executeUpdate();
                    refreshTableData();
                    JOptionPane.showMessageDialog(this, "Запис успішно додано!");
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка додавання запису: " + ex.getMessage(),
                    "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUpdateDialog() {
        String tableName = (String) tableComboBox.getSelectedItem();
        if (tableName == null) return;

        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Будь ласка, виберіть запис для редагування",
                    "Попередження", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = getConnection()) {
            // Отримати інформацію про стовпці
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);

            List<String> columnNames = new ArrayList<>();
            List<String> columnTypes = new ArrayList<>();
            List<Boolean> isAutoIncrement = new ArrayList<>();

            while (columns.next()) {
                columnNames.add(columns.getString("COLUMN_NAME"));
                columnTypes.add(columns.getString("TYPE_NAME"));
                isAutoIncrement.add("YES".equals(columns.getString("IS_AUTOINCREMENT")));
            }

            // Отримати первинний ключ
            ResultSet pkResult = metaData.getPrimaryKeys(null, null, tableName);
            String pkColumn = null;
            if (pkResult.next()) {
                pkColumn = pkResult.getString("COLUMN_NAME");
            }

            if (pkColumn == null) {
                JOptionPane.showMessageDialog(this, "Не вдалося визначити первинний ключ для таблиці",
                        "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Створити діалогове вікно
            JPanel panel = new JPanel(new GridLayout(columnNames.size(), 2, 5, 5));
            List<JTextField> inputs = new ArrayList<>();

            for (int i = 0; i < columnNames.size(); i++) {
                Object value = dataTable.getValueAt(selectedRow, i);
                JTextField textField = new JTextField(value != null ? value.toString() : "", 20);

                panel.add(new JLabel(columnNames.get(i) + " (" + columnTypes.get(i) + "):"));
                panel.add(textField);

                if (isAutoIncrement.get(i)) {
                    textField.setEditable(false);
                }

                inputs.add(textField);
            }

            int result = JOptionPane.showConfirmDialog(
                    this, panel, "Редагувати запис у " + tableName,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                // Побудувати SQL запит
                StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");

                int paramIndex = 1;
                for (int i = 0; i < columnNames.size(); i++) {
                    if (isAutoIncrement.get(i)) continue;

                    if (paramIndex > 1) {
                        sql.append(", ");
                    }

                    sql.append(columnNames.get(i)).append(" = ?");
                    paramIndex++;
                }

                sql.append(" WHERE ").append(pkColumn).append(" = ?");

                // Виконати запит
                try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                    paramIndex = 1;

                    // Встановити значення для SET
                    for (int i = 0; i < columnNames.size(); i++) {
                        if (isAutoIncrement.get(i)) continue;

                        String value = inputs.get(i).getText();
                        setParameter(stmt, paramIndex, value, columnTypes.get(i));
                        paramIndex++;
                    }

                    // Встановити значення для WHERE (первинний ключ)
                    Object pkValue = dataTable.getValueAt(selectedRow, getColumnIndex(pkColumn));
                    stmt.setObject(paramIndex, pkValue);

                    stmt.executeUpdate();
                    refreshTableData();
                    JOptionPane.showMessageDialog(this, "Запис успішно оновлено!");
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка оновлення запису: " + ex.getMessage(),
                    "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedRecord() {
        String tableName = (String) tableComboBox.getSelectedItem();
        if (tableName == null) return;

        int selectedRow = dataTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Будь ласка, виберіть запис для видалення",
                    "Попередження", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = getConnection()) {
            // Отримати первинний ключ
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet pkResult = metaData.getPrimaryKeys(null, null, tableName);
            String pkColumn = null;
            if (pkResult.next()) {
                pkColumn = pkResult.getString("COLUMN_NAME");
            }

            if (pkColumn == null) {
                JOptionPane.showMessageDialog(this, "Не вдалося визначити первинний ключ для таблиці",
                        "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Підтвердити видалення
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Ви впевнені, що хочете видалити обраний запис?",
                    "Підтвердження видалення", JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            // Виконати видалення
            String sql = "DELETE FROM " + tableName + " WHERE " + pkColumn + " = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                Object pkValue = dataTable.getValueAt(selectedRow, getColumnIndex(pkColumn));
                stmt.setObject(1, pkValue);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    refreshTableData();
                    JOptionPane.showMessageDialog(this, "Запис успішно видалено!");
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Помилка видалення запису: " + ex.getMessage(),
                    "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getColumnIndex(String columnName) {
        for (int i = 0; i < dataTable.getColumnCount(); i++) {
            if (dataTable.getColumnName(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private void setParameter(PreparedStatement stmt, int index, String value, String sqlType)
            throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            stmt.setNull(index, Types.NULL);
            return;
        }

        switch (sqlType.toUpperCase()) {
            case "INT":
            case "BIGINT":
            case "SMALLINT":
            case "TINYINT":
                stmt.setInt(index, Integer.parseInt(value));
                break;
            case "FLOAT":
            case "REAL":
            case "DOUBLE":
                stmt.setDouble(index, Double.parseDouble(value));
                break;
            case "DECIMAL":
            case "NUMERIC":
            case "MONEY":
                stmt.setBigDecimal(index, new java.math.BigDecimal(value));
                break;
            case "BIT":
            case "BOOLEAN":
                stmt.setBoolean(index, Boolean.parseBoolean(value));
                break;
            case "DATE":
                stmt.setDate(index, java.sql.Date.valueOf(value));
                break;
            case "TIME":
                stmt.setTime(index, java.sql.Time.valueOf(value));
                break;
            case "DATETIME":
            case "DATETIME2":
            case "SMALLDATETIME":
            case "TIMESTAMP":
                stmt.setTimestamp(index, java.sql.Timestamp.valueOf(value));
                break;
            default:
                stmt.setString(index, value);
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            SwingUtilities.invokeLater(Main::new);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Драйвер SQL Server не знайдено!");
        }
    }
}