package com.norbitltd.spoiwo.natures.xlsx

import com.norbitltd.spoiwo.model.Measure._
import org.apache.poi.ss.usermodel
import Model2XlsxConversions.convertCell
import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFWorkbook}
import com.norbitltd.spoiwo.model.{Font, CellStyle, Cell}
import org.scalatest.FlatSpec

class Model2XlsxConversionsForCellSpec extends FlatSpec {

  private def row = new XSSFWorkbook().createSheet().createRow(0)

  private def convert(cell: Cell): XSSFCell = convertCell(cell, row)

  private val defaultCell = convert(Cell.Empty)

  "Cell conversion" should "return string cell type with empty string by default" in {
    assert(defaultCell.getCellType == usermodel.Cell.CELL_TYPE_STRING)
    assert(defaultCell.getStringCellValue == "")
  }

  it should "return a 0 column index by default if no other cells specified in the row" in {
    assert(defaultCell.getColumnIndex == 0)
  }

  it should "return cell style with 11pt Calibri by default" in {
    assert(defaultCell.getCellStyle.getFont.getFontHeightInPoints === 11)
    assert(defaultCell.getCellStyle.getFont.getFontName === "Calibri")
  }

  it should "return cell style with 14pt Arial when explicitly specified" in {
    val cellStyle = CellStyle(font = Font(fontName = "Arial", height = 14 points))
    val model = Cell.Empty.withStyle(cellStyle)
    val xlsx = convert(model)

    assert(xlsx.getCellStyle.getFont.getFontHeightInPoints == 14)
    assert(xlsx.getCellStyle.getFont.getFontName == "Arial")
  }

  it should "return index of 3 when explicitly specified" in {
    val model = Cell.Empty.withIndex(3)
    val xlsx = convert(model)

    assert(xlsx.getColumnIndex == 3)
  }

  it should "return index of 2 when row has already 2 other cells" in {
    val row = new XSSFWorkbook().createSheet().createRow(0)
    row.createCell(0)
    row.createCell(1)

    val model = Cell.Empty
    val xlsx = convertCell(model, row)
    assert(xlsx.getColumnIndex == 2)
  }

  it should "return string cell when set up with 'String'" in {
    val model = Cell("TEST_STRING")
    val xlsx = convert(model)
    assert(xlsx.getCellType == usermodel.Cell.CELL_TYPE_STRING)
    assert(xlsx.getStringCellValue == "TEST_STRING")
  }

  it should "return formula cell when set up with string starting with '=' sign" in {
    val model = Cell("=1000/3+7")
    val xlsx = convert(model)
    assert(xlsx.getCellType == usermodel.Cell.CELL_TYPE_FORMULA)
    assert(xlsx.getCellFormula == "1000/3+7")
  }

  it should "return numeric cell when set up with double value" in {
    val model = Cell(90.45)
    val xlsx = convert(model)
    assert(xlsx.getCellType == usermodel.Cell.CELL_TYPE_NUMERIC)
    assert(xlsx.getNumericCellValue == 90.45)
  }

  it should "return numeric cell when set up with int value" in {
    val model = Cell(90)
    val xlsx = convert(model)
    assert(xlsx.getCellType == usermodel.Cell.CELL_TYPE_NUMERIC)
    assert(xlsx.getNumericCellValue == 90)
  }

  it should "return numeric cell when set up with long value" in {
    val model = Cell(10000000000000l)
    val xlsx = convert(model)
    assert(xlsx.getCellType == usermodel.Cell.CELL_TYPE_NUMERIC)
    assert(xlsx.getNumericCellValue == 10000000000000l)
  }

}
