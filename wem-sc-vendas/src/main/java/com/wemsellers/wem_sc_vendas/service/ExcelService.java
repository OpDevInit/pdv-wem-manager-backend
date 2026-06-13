package com.wemsellers.wem_sc_vendas.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.wemsellers.wem_sc_vendas.dto.FechamentoVendaDTO;
import com.wemsellers.wem_sc_vendas.dto.ItemCarrinhoDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;

    @Service
public class ExcelService {

    private final String CAMINHO_ARQUIVO = "vendas.xlsx";

    public void registrarVenda(FechamentoVendaDTO venda) {
        Workbook workbook;
        Sheet sheet;
        File arquivo = new File(CAMINHO_ARQUIVO);

        // Se o arquivo já existir, abre. Se não, cria um novo.
        try {
            if (arquivo.exists()) {
                FileInputStream fis = new FileInputStream(arquivo);
                workbook = WorkbookFactory.create(fis);
                sheet = workbook.getSheetAt(0);
                fis.close();
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Vendas");
                // Cria o cabeçalho
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Produto");
                header.createCell(1).setCellValue("Quantidade");
                header.createCell(2).setCellValue("Valor Unitário");
                header.createCell(3).setCellValue("Valor Total");
                header.createCell(4).setCellValue("Forma de Pagamento");
            }

            // Descobre a última linha preenchida para continuar de lá
            int proximaLinha = sheet.getLastRowNum() + 1;

            // Grava cada item da venda na planilha
            for (ItemCarrinhoDTO item : venda.getItens()) {
                Row row = sheet.createRow(proximaLinha++);
                row.createCell(0).setCellValue(item.getNome());
                row.createCell(1).setCellValue(item.getQuantidade());
                row.createCell(2).setCellValue(item.getPrecoUnitario());
                row.createCell(3).setCellValue(item.getQuantidade() * item.getPrecoUnitario());
                row.createCell(4).setCellValue(venda.getFormaPagamento());
            }

            // Salva as alterações no arquivo
            FileOutputStream fos = new FileOutputStream(arquivo);
            workbook.write(fos);
            workbook.close();
            fos.close();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao manipular o arquivo Excel", e);
        }
    }
}

