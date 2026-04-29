const db = require('../database');

exports.setBudget = (req, res) => {
  const { month_year, amount } = req.body;
  if (amount < 0) return res.status(400).json({ message: 'Budget invalide' });
  db.run(`INSERT OR REPLACE INTO monthly_budgets (month_year, amount, user_id) VALUES (?,?,?)`,
    [month_year, amount, req.userId],
    function(err) {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ message: 'Budget enregistré' });
    });
};

exports.getBudget = (req, res) => {
  const { month_year } = req.params;
  db.get(`SELECT amount FROM monthly_budgets WHERE user_id=? AND month_year=?`, [req.userId, month_year], (err, row) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json({ amount: row ? row.amount : 0 });
  });
};

exports.getCurrentMonthSpending = (req, res) => {
  const now = new Date();
  const month_year = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}`;
  db.get(`SELECT SUM(amount) as total FROM expenses WHERE user_id=? AND strftime('%Y-%m', date) = ?`,
    [req.userId, month_year], (err, row) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json({ spent: row.total || 0, month: month_year });
    });
};