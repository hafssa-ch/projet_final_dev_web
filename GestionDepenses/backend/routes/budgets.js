const express = require('express');
const auth = require('../middleware/auth');
const budgetController = require('../controllers/budgetController');
const router = express.Router();

router.use(auth);
router.post('/', budgetController.setBudget);
router.get('/current-spending', budgetController.getCurrentMonthSpending);
router.get('/:month_year', budgetController.getBudget);

module.exports = router;