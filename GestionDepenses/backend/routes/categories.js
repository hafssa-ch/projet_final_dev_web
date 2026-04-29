const express = require('express');
const auth = require('../middleware/auth');
const categoryController = require('../controllers/categoryController');
const router = express.Router();

router.use(auth);
router.get('/', categoryController.getCategories);
router.post('/', categoryController.createCategory);
router.delete('/:id', categoryController.deleteCategory);

module.exports = router;
