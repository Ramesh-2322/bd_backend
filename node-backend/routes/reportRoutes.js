const express = require("express");
const auth = require("../middleware/auth");
const upload = require("../middleware/upload");
const { uploadReport } = require("../controllers/reportController");

const router = express.Router();

router.post("/upload", auth, upload.single("file"), uploadReport);

module.exports = router;
