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

	public List<Stock> getStocks1() {
		// Hibernate 5.x and higher supports Java try-with-resources
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			List<Stock> stocks = session.createQuery("From Stock").list();
			
			// NOTE: If we comment out "printStocks()", then "failed to lazily initialize a collection of role"
			System.out.println("Inside getStocks(), #/stocks=" + stocks.size());
			printStocks(stocks);
			return stocks;
		}
	}
	
	public List<Stock> getStocks2() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			List<Stock> stocks = session.createQuery("SELECT DISTINCT s From Stock s INNER JOIN FETCH s.stockDailyRecords").list();
			return stocks;
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
	
	public void saveStock(String[] stockData, String[] reportData) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			try {
			session.beginTransaction();

			Stock stock = new Stock();
			stock.setStockCode(stockData[0]);
			stock.setStockName(stockData[1]);
			session.save(stock);
        
			int idx = 0;
			while (idx < reportData.length) {
				StockDailyRecord stockDailyRecord = new StockDailyRecord();
				stockDailyRecord.setPriceOpen(Float.parseFloat(reportData[idx]));
				stockDailyRecord.setPriceClose(Float.parseFloat(reportData[idx+1]));
				stockDailyRecord.setPriceChange(Float.parseFloat(reportData[idx+2]));
				stockDailyRecord.setVolume(Long.parseLong(reportData[idx+3]));
				stockDailyRecord.setDate(new Date());
				stockDailyRecord.setStock(stock);        
				stock.getStockDailyRecords().add(stockDailyRecord);
				session.save(stockDailyRecord);
				idx += 4;
			}

			session.getTransaction().commit();
			} catch (Exception e) {
				System.out.println("ERROR: Aborting transaction: " + e.getMessage());
				session.getTransaction().rollback();
				throw e;
			}
		}
	}

	public static void main(String[] args) {
		try {
			// NOTE: Delete test data between runs...
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
			app.saveStock(
					new String[] {
						"777",
						"SVN"
					}, 
					new String[] {});
			app.saveStock(
					new String[] {
						"666",
						"SIX"
					},
					new String[] {
						"10.5",
						"11.5",
						"1.0",
						"5000000",
						"12.5",
						"13.5",
						"1.0",
						"5000000"
					});
			
			System.out.println("Reading data...");
			List<Stock> stocks = app.getStocks1();
			System.out.println("Back in main(), #/stocks=" + stocks.size());
			app.printStocks(stocks);

			System.out.println("INNER JOIN FETCH query...");
			stocks = app.getStocks2();
			System.out.println("Back in main(), #/stocks=" + stocks.size());
			app.printStocks(stocks);
			
			System.out.println("Done");
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
