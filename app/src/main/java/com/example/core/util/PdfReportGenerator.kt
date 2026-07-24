package com.example.core.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.Typeface
import com.example.ui.screens.reports.ReportData
import com.example.ui.screens.reports.ReportRow
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Generates PDF reports using Android's PdfDocument API.
 * Works completely offline with no external dependencies.
 */
object PdfReportGenerator {

    private const val PAGE_WIDTH = 595  // A4 width in points
    private const val PAGE_HEIGHT = 842 // A4 height in points
    private const val MARGIN = 40f
    private const val LINE_HEIGHT = 20f
    private const val HEADER_HEIGHT = 30f

    private val titlePaint = Paint().apply {
        color = Color.BLACK
        textSize = 20f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    private val headerPaint = Paint().apply {
        color = Color.rgb(33, 33, 33)
        textSize = 14f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    private val bodyPaint = Paint().apply {
        color = Color.rgb(66, 66, 66)
        textSize = 12f
        typeface = Typeface.DEFAULT
        isAntiAlias = true
    }

    private val amountPaint = Paint().apply {
        color = Color.rgb(33, 33, 33)
        textSize = 12f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
        textAlign = Paint.Align.RIGHT
    }

    private val subtitlePaint = Paint().apply {
        color = Color.rgb(100, 100, 100)
        textSize = 10f
        typeface = Typeface.DEFAULT
        isAntiAlias = true
    }

    private val linePaint = Paint().apply {
        color = Color.rgb(200, 200, 200)
        strokeWidth = 1f
    }

    /**
     * Generates a PDF file from report data.
     * @param context Android context for file access
     * @param reportData The report data to render
     * @return The File path of the generated PDF
     */
    fun generatePdf(context: Context, reportData: ReportData): File {
        val document = PdfDocument()
        var pageNumber = 1
        var currentPage = createPage(document, pageNumber)
        var canvas = currentPage.canvas
        var yPosition = MARGIN + 10f

        // Title
        canvas.drawText(reportData.title, MARGIN, yPosition + 20f, titlePaint)
        yPosition += 35f

        // Subtitle with date
        val dateStr = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()).format(Date(reportData.generatedAt))
        canvas.drawText("Generated: $dateStr", MARGIN, yPosition + 12f, subtitlePaint)
        yPosition += 20f

        // Filter info
        if (reportData.filter.projectName != null) {
            canvas.drawText("Project: ${reportData.filter.projectName}", MARGIN, yPosition + 12f, subtitlePaint)
            yPosition += 18f
        }
        if (reportData.filter.startDate != null || reportData.filter.endDate != null) {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val rangeStr = buildString {
                append("Period: ")
                if (reportData.filter.startDate != null) append(sdf.format(Date(reportData.filter.startDate)))
                else append("All")
                append(" to ")
                if (reportData.filter.endDate != null) append(sdf.format(Date(reportData.filter.endDate)))
                else append("Present")
            }
            canvas.drawText(rangeStr, MARGIN, yPosition + 12f, subtitlePaint)
            yPosition += 18f
        }

        // Separator
        yPosition += 10f
        canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, linePaint)
        yPosition += 15f

        // Company Header
        canvas.drawText("DEYAAR CONSTRUCTIONS - Building Your Vision", MARGIN, yPosition + 14f, headerPaint)
        yPosition += 30f

        // Table Header
        canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, linePaint)
        yPosition += 5f
        canvas.drawText("Description", MARGIN, yPosition + 12f, headerPaint)
        canvas.drawText("Amount", PAGE_WIDTH - MARGIN, yPosition + 12f, amountPaint)
        yPosition += 20f
        canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, linePaint)
        yPosition += 10f

        // Rows
        for (row in reportData.rows) {
            // Check if we need a new page
            if (yPosition > PAGE_HEIGHT - MARGIN - 60f) {
                document.finishPage(currentPage)
                pageNumber++
                currentPage = createPage(document, pageNumber)
                canvas = currentPage.canvas
                yPosition = MARGIN + 20f
            }

            // Label
            canvas.drawText(row.label, MARGIN, yPosition + 12f, bodyPaint)

            // Amount (if non-zero)
            if (row.amountPaise != 0L) {
                val amountStr = CurrencyUtils.formatPaise(row.amountPaise)
                canvas.drawText(amountStr, PAGE_WIDTH - MARGIN, yPosition + 12f, amountPaint)
            }

            // Quantity (if present)
            if (row.quantity != null) {
                canvas.drawText(row.quantity, PAGE_WIDTH - MARGIN, yPosition + 12f, amountPaint)
            }

            yPosition += LINE_HEIGHT

            // Sublabel
            if (row.sublabel != null) {
                canvas.drawText(row.sublabel, MARGIN + 10f, yPosition + 10f, subtitlePaint)
                yPosition += 16f
            }

            // Light separator
            canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, linePaint)
            yPosition += 8f
        }

        // Summary section
        yPosition += 15f
        if (yPosition > PAGE_HEIGHT - MARGIN - 100f) {
            document.finishPage(currentPage)
            pageNumber++
            currentPage = createPage(document, pageNumber)
            canvas = currentPage.canvas
            yPosition = MARGIN + 20f
        }

        canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, linePaint)
        yPosition += 5f
        canvas.drawText("SUMMARY", MARGIN, yPosition + 14f, headerPaint)
        yPosition += 25f

        for (line in reportData.summaryLines) {
            canvas.drawText(line, MARGIN, yPosition + 12f, bodyPaint)
            yPosition += LINE_HEIGHT
        }

        // Total
        yPosition += 10f
        canvas.drawLine(MARGIN, yPosition, PAGE_WIDTH - MARGIN, yPosition, linePaint)
        yPosition += 5f
        canvas.drawText("Total: ${CurrencyUtils.formatPaise(reportData.totalAmountPaise)}", MARGIN, yPosition + 14f, headerPaint)

        document.finishPage(currentPage)

        // Write to file
        val exportDir = File(context.getExternalFilesDir(null), "reports")
        if (!exportDir.exists()) exportDir.mkdirs()

        val fileName = "${reportData.title.replace(" ", "_").replace("/", "_")}_${System.currentTimeMillis()}.pdf"
        val file = File(exportDir, fileName)

        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()

        return file
    }

    private fun createPage(document: PdfDocument, pageNumber: Int): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        return document.startPage(pageInfo)
    }
}
