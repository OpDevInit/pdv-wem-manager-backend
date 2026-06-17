package com.wemsellers.wem_sc_vendas.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


import org.apache.poi.ss.usermodel.*;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.wemsellers.wem_sc_vendas.dto.FechamentoVendaDTO;
import com.wemsellers.wem_sc_vendas.dto.ItemCarrinhoDTO;
import com.wemsellers.wem_sc_vendas.dto.VendaDTO;
import com.wemsellers.wem_sc_vendas.model.Produto;
import com.wemsellers.wem_sc_vendas.repository.ProdutoRepository;
import com.wemsellers.wem_sc_vendas.service.ExcelService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/pdv")
@CrossOrigin("*")
public class PVDController {


    
    
    private final ProdutoRepository produtoRepository;

    private final ExcelService excelService;

    PVDController(ProdutoRepository produtoRepository, ExcelService excelService) {
        this.produtoRepository = produtoRepository;
        this.excelService = excelService;
    }

    // --- ENDPOINT: LISTAR TODOS OS PRODUTOS ---
    @GetMapping("/produtos")
public List<Produto> listarProdutos() {
    // Usando o import do Sort para organizar de A a Z pelo atributo "nome"
    return produtoRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"));
}

    // --- ENDPOINT: CADASTRO DE PRODUTO ---
    @PostMapping("/produtos")
   public Produto cadastrarProduto(@RequestBody Produto produto) {
        return produtoRepository.save(produto); // Salva direto na Supabase
    }

    // 1. BIPAR CÓDIGO DE BARRAS: Retorna o produto e a quantidade padrão (1) para o
 @GetMapping("/bipar/{codigo}")
public ResponseEntity<ItemCarrinhoDTO> biparProduto(@PathVariable String codigo) {
    String codigoLimpo = codigo.trim();
    
    // 1. Tenta buscar pelo código exato que você digitou/bipou (ex: "002")
    java.util.Optional<Produto> produtoOpt = produtoRepository.findByCodigoBarras(codigoLimpo);
    
    // 2. Se não achou e o código for só números, limpa os zeros à esquerda e tenta de novo (ex: "2")
    if (produtoOpt.isEmpty() && codigoLimpo.matches("\\d+")) {
        String codigoSemZeros = String.valueOf(Long.parseLong(codigoLimpo));
        produtoOpt = produtoRepository.findByCodigoBarras(codigoSemZeros);
    }
    
    // 3. Se achou em alguma das tentativas, retorna o DTO. Se não, retorna 404.
    return produtoOpt
            .map(p -> ResponseEntity.ok(new ItemCarrinhoDTO(p.getNome(), 1, p.getPreco())))
            .orElse(ResponseEntity.notFound().build());
}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    produtoRepository.delete(produto);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. ROTA PARA EDITAR PRODUTO PELO ID
    @PutMapping("/{id}")
    public ResponseEntity<Produto> editarProduto(@PathVariable Long id, @RequestBody Produto dadosAtualizados) {
        return produtoRepository.findById(id)
                .map(produtoExistente -> {
                    // Atualiza apenas os campos permitidos
                    produtoExistente.setNome(dadosAtualizados.getNome());
                    produtoExistente.setPreco(dadosAtualizados.getPreco());
                    
                    Produto produtoSalvo = produtoRepository.save(produtoExistente);
                    return ResponseEntity.ok(produtoSalvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    // 2. FINALIZAR VENDA: Recebe a lista de produtos do PDV e salva no Excel
    @PostMapping("/finalizar")
    public ResponseEntity<byte[]> finalizarVenda(@RequestBody VendaDTO dadosVenda) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Itens Vendidos");

            // 1. Criar o Cabeçalho do Excel
            Row headerRow = sheet.createRow(0);
            String[] colunas = {"Produto", "Quantidade", "Preço Unitário", "Total Item", "Forma de Pagamento"};
            for (int i = 0; i < colunas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(colunas[i]);
                
                // Estilo básico para o cabeçalho em negrito
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // 2. Preencher as linhas com os itens do carrinho
            // Dentro do método finalizarVenda do seu PdvController:

            int rowNum = 1;
            double totalGeral = 0;
            for (ItemCarrinhoDTO item : dadosVenda.getItens()) {
                Row row = sheet.createRow(rowNum++);
                double totalItem = item.getQuantidade() * item.getPrecoUnitario();
                totalGeral += totalItem;

                row.createCell(0).setCellValue(item.getNome());
                row.createCell(1).setCellValue(item.getQuantidade());
                row.createCell(2).setCellValue(item.getPrecoUnitario());
                row.createCell(3).setCellValue(totalItem);

                // ALTERAÇÃO AQUI: Se o ItemCarrinhoDTO tiver a forma de pagamento dele, usamos
                // ela!
                row.createCell(4).setCellValue(
                        item.getFormaPagamento() != null ? item.getFormaPagamento() : dadosVenda.getFormaPagamento());
            }

            // 3. Linha de Total Geral no fim do relatório
            Row totalRow = sheet.createRow(rowNum + 1);
            totalRow.createCell(2).setCellValue("TOTAL DA VENDA:");
            totalRow.createCell(3).setCellValue(totalGeral);

            // Ajustar largura das colunas automaticamente
            for (int i = 0; i < colunas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 4. Escrever o arquivo em um array de bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] arquivoBytes = outputStream.toByteArray();

            // 5. Definir o nome do arquivo com a data/hora atual para não sobrescrever
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nomeArquivo = "venda_" + dataHora + ".xlsx";

            // 6. Configurar os Headers HTTP para forçar o Download no navegador/celular
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", nomeArquivo);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(arquivoBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
