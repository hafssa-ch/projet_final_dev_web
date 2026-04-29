const express = require('express');
const auth = require('../middleware/auth');
const expenseController = require('../controllers/expenseController');
const router = express.Router();

router.use(auth);
router.get('/', expenseController.getExpenses);
router.post('/', expenseController.createExpense);
router.put('/:id', expenseController.updateExpense);
router.delete('/:id', expenseController.deleteExpense);

module.exports = router;