const db = require('../database');

exports.getMonthlyStats = (req, res) => {
  const { year } = req.query;
  const currentYear = year || new Date().getFullYear();
  db.all(`SELECT strftime('%m', date) as month, SUM(amount) as total
           FROM expenses WHERE user_id = ? AND strftime('%Y', date) = ?
           GROUP BY month ORDER BY month`,
    [req.userId, currentYear], (err, rows) => {
      if (err) return res.status(500).json({ error: err.message });
      res.json(rows);
    });
};

exports.getCategoryBreakdown = (req, res) => {
  const { month_year } = req.query;
  let sql = `SELECT c.name, c.color, SUM(e.amount) as total
             FROM expenses e JOIN expense_categories c ON e.category_id = c.id
             WHERE e.user_id = ?`;
  let params = [req.userId];
  if (month_year) {
    sql += ` AND strftime('%Y-%m', e.date) = ?`;
    params.push(month_year);
  }
  sql += ` GROUP BY c.id ORDER BY total DESC`;
  db.all(sql, params, (err, rows) => {
    if (err) return res.status(500).json({ error: err.message });
    res.json(rows);
  });
};

exports.getBudgetVsActual = (req, res) => {
  const { month_year } = req.query;
  if (!month_year) return res.status(400).json({ message: 'month_year requis' });
  db.get(`SELECT amount FROM monthly_budgets WHERE user_id=? AND month_year=?`, [req.userId, month_year], (err, budgetRow) => {
    db.get(`SELECT SUM(amount) as actual FROM expenses WHERE user_id=? AND strftime('%Y-%m', date)=?`, [req.userId, month_year], (err2, expenseRow) => {
      res.json({
        budget: budgetRow ? budgetRow.amount : 0,
        actual: expenseRow ? expenseRow.actual : 0
      });
    });
  });
};