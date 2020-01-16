package com.mkyong;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import com.mkyong.stock.Stock;
import com.mkyong.stock.StockDailyRecord;
import com.mkyong.util.HibernateUtil;

/**
 * Hibernate One-to-Many/XML example
 * 
 * REFERENCE:
 * https://mkyong.com/hibernate/hibernate-one-to-many-relationship-example/
 */
public class App {

	public void saveStock(String[] stockData, String[] reportData) {
		// Hibernate 5.x and higher supports Java try-with-resources
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			try {
			session.beginTransaction();

			Stock stock = new Stock();
			stock.setStockCode(stockData[0]);
			stock.setStockName(stockData[1]);
			session.save(stock);
        
			StockDailyRecord stockDailyRecords = new StockDailyRecord();
			stockDailyRecords.setPriceOpen(Float.parseFloat(reportData[0]));
			stockDailyRecords.setPriceClose(Float.parseFloat(reportData[1]));
			stockDailyRecords.setPriceChange(Float.parseFloat(reportData[2]));
			stockDailyRecords.setVolume(Long.parseLong(reportData[3]));
			stockDailyRecords.setDate(new Date());
        
			stockDailyRecords.setStock(stock);        
			stock.getStockDailyRecords().add(stockDailyRecords);

			session.save(stockDailyRecords);
			session.getTransaction().commit();
			} catch (Exception e) {
				System.out.println("ERROR: Aborting transaction: " + e.getMessage());
				session.getTransaction().rollback();
				throw e;
			}
		}
	}
	
	public void printStocks(List<Stock> stocks) {
		for (Stock s : stocks) {
			System.out.println("Stock code: " + s.getStockCode() + ", name: " + s.getStockName());
			for (StockDailyRecord sdr : s.getStockDailyRecords()) {
				System.out.println("  Open: " + sdr.getPriceOpen() + ", close: " + sdr.getPriceClose() + ", volume: " + sdr.getVolume());
			}
		}
	}
	
	public List<Stock> getStocks() {
		// Hibernate 5.x and higher supports Java try-with-resources
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			List<Stock> stocks = session.createQuery("From Stock").list();
			System.out.println("Inside getStocks(), #/stocks=" + stocks.size());
			printStocks(stocks);
			return stocks;
		}
	}
	
	public static void main(String[] args) {
		try {
			System.out.println("Saving data...");
			App app = new App ();
			app.saveStock(
				new String[] {
					"7052",
					"PADINI"
				},
				new String[] {
					"1.2",
					"1.1",
					"10.0",
					"3000000"
				});
			app.saveStock(
					new String[] {
						"888",
						"MSFT"
					},
					new String[] {
						"162.62",
						"163.18",
						"0.56",
						"5000000"
					});
			
			System.out.println("Reading data...");
			List<Stock> stocks = app.getStocks();
			System.out.println("Back in main(), #/stocks=" + stocks.size());
			app.printStocks(stocks);
			
			System.out.println("Done");
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
