/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MLP;

import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author luizf
 */
public class Grafico {
    
    DefaultCategoryDataset graficoLinhas = new DefaultCategoryDataset();
    public void adicionaDados(double erroQuadradoMedio, String categoria, String epoca){
        graficoLinhas.addValue(erroQuadradoMedio, categoria, epoca);
    }
    public void criaGrafico(){
       
        JFreeChart chart = ChartFactory.createLineChart("Erro quadrado médio", "Épocas", "Erro", graficoLinhas, PlotOrientation.VERTICAL, true,true, false);
        chart.setBackgroundPaint(Color.gray);
        chart.getTitle().setPaint(Color.blue);
        CategoryPlot p = chart.getCategoryPlot();
        p.setForegroundAlpha(0.9f);
        p.setRangeGridlinePaint(Color.red);
        p.setDomainGridlinesVisible(false);
        p.setDomainGridlinePaint(Color.black);
        CategoryItemRenderer renderer = p.getRenderer();
        renderer.setSeriesPaint(1, Color.red);
        renderer.setSeriesPaint(0, Color.green);
        ChartFrame frame1 = new ChartFrame("Gráfico de linhas", chart);
        
        frame1.setSize(1300,1000);
        
        frame1.setVisible(true);
    }
    
    
    
    
    
}
