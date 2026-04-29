const db = require('../database');

exports.getExpenses = (req, res) => {
  const { startDate, endDate, categoryId } = req.query;
  let sql = `SELECT e.*, c.name as category_name, c.color FROM expenses e JOIN expense_categories c ON e.category_id = c.id WHERE e.user_id = ?`;
  let params = [req.userId];
  if (startDate) { sql += ` AND e.date >= ?`; params.push(startDate); }
  if (endDate) { sql += ` AND e.date <= ?`; params.push(endDate); }
  if (categoryId) { sql += ` AND e.category_id = ?`; params.push(categoryId); }
  sql += ` ORDER BY e.date DESC`;
  db.all(sql, params, (err, rows) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(rows);
  });
};

exports.createExpense = (req, res) => {
  const { amount, date, note, category_id } = req.body;
  if (amount <= 0) return res.status(400).json({ message: 'Montant > 0' });
  db.run(`INSERT INTO expenses (amount, date, note, user_id, category_id) VALUES (?,?,?,?,?)`,
    [amount, date, note, req.userId, category_id],
    function(err) {
      if (err) return res.status(500).json({ error: err.message });
      res.status(201).json({ id: this.lastID });
    });
};

exports.updateExpense = (req, res) => {
  const { amount, date, note, category_id } = req.body;
  if (amount <= 0) return res.status(400).json({ message: 'Montant > 0' });
  db.run(`UPDATE expenses SET amount=?, date=?, note=?, category_id=? WHERE id=? AND user_id=?`,
    [amount, date, note, category_id, req.params.id, req.userId],
    function(err) {
      if (err) return res.status(500).json({ error: err.message });
      if (this.changes === 0) return res.status(404).json({ message: 'Dépense non trouvée' });
      res.json({ message: 'Dépense modifiée' });
    });
};

exports.deleteExpense = (req, res) => {
  db.run(`DELETE FROM expenses WHERE id=? AND user_id=?`, [req.params.id, req.userId], function(err) {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ message: 'Dépense supprimée' });
  });
};