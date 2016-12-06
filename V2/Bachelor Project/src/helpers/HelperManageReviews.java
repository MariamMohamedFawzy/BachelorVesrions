package helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import json.Business;
import json.Category;
import json.Review;
import liuyang.nlp.lda.main.LdaModel;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import Engine.WordTopic;
import GUI.GUIFunctions;

public class HelperManageReviews {

	public static void saveReviews(ArrayList<Review> reviews) {
		SessionFactory factory = getSessionFactory();
		if (factory != null) {
			Session session = factory.openSession();
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				int counterReviews = 1;
				for (Review review : reviews) {
					review.setId(counterReviews);
					session.save(review);
					counterReviews++;
				}
				tx.commit();
			} catch (HibernateException e) {
				if (tx != null)
					try {
						tx.rollback();
					} catch (IllegalStateException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
	}

	public static void saveReviews(ArrayList<Review> reviews, int startCounter) {
		SessionFactory factory = getSessionFactory();
		if (factory != null) {
			Session session = factory.openSession();
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				int counterReviews = startCounter;
				for (Review review : reviews) {
					review.setId(counterReviews);
					session.save(review);
					counterReviews++;
				}
				tx.commit();
			} catch (HibernateException e) {
				if (tx != null)
					try {
						tx.rollback();
					} catch (IllegalStateException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
	}

	public static void updateReviewSentimentText(int[] reviewsLength) {
		SessionFactory factory = getSessionFactory();
		if (factory != null) {
			HelperLDA.init();
			for (int i = 0; i < reviewsLength.length - 1; i++) {
				int start = reviewsLength[i];
				int end = reviewsLength[i + 1];
				Session session = factory.openSession();
				Transaction tx = null;
				try {
					tx = session.beginTransaction();
					for (int j = start; j < end; j++) {
						Review review = (Review) session.get(Review.class, j);
						review.setSentimentText(HelperLDA
								.getStringWithSentiment(review.getText()));
						session.update(review);
					}
					tx.commit();
				} catch (HibernateException e) {
					if (tx != null)
						tx.rollback();
					e.printStackTrace();
				} finally {
					session.close();
				}
			}
		}
	}

	public static void saveBusinesses(ArrayList<Business> businesses) {
		SessionFactory factory = getSessionFactory();
		if (factory != null) {
			Session session = factory.openSession();
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				int counterBusinesses = 1;
				int counterCategories = 1;
				for (int i = 0; i < businesses.size(); i++) {
					Business business = businesses.get(i);
					business.setId(counterBusinesses);
					session.save(business);
					for (Category category : business.getListCategories()) {
						category.setId(counterCategories);
						category.setBusinessId(counterBusinesses);
						session.save(category);
						counterCategories++;
					}
					counterBusinesses++;
				}
				System.out.println("counter businesses = " + counterBusinesses);
				tx.commit();
			} catch (HibernateException e) {
				if (tx != null)
					try {
						tx.rollback();
					} catch (IllegalStateException e1) {
						e1.printStackTrace();
					}
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
	}

	public static ArrayList<String> getDistinctCategories() {
		ArrayList<String> categories = new ArrayList<String>();

		SessionFactory factory = getSessionFactory();
		if (factory != null) {
			Session session = factory.openSession();
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				Criteria c = session.createCriteria(Category.class);
				c.setProjection(Projections.distinct(Projections
						.property("name")));
				c.addOrder(Order.asc("name"));

				List<String> listCategories = c.list();

				tx.commit();

				for (String categoryName : listCategories) {
					if (!categoryName.equalsIgnoreCase("")) {
						categories.add(categoryName);
					}
				}
			} catch (HibernateException e) {
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
//		System.out.println(categories.size() + " 000");
		return categories;
	}

	public static ArrayList<Business> readBusinesses() {
		ArrayList<Business> businesses = new ArrayList<Business>();

		SessionFactory factory = getSessionFactory();
		if (factory != null) {
			Session session = factory.openSession();
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				String hql = "FROM json.Business";
				Query query = session.createQuery(hql);
				List results = query.list();
				businesses = (ArrayList<Business>) results;
				tx.commit();
			} catch (HibernateException e) {
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
		return businesses;
	}

	public static ArrayList<Review> getReviewsByCategoryAndLocation(
			String category, double lng, double lat, boolean considerLocation) {
		ArrayList<Review> result = new ArrayList<Review>();
		SessionFactory factory = getSessionFactory();
		if (factory != null) {
			Session session = factory.openSession();
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				String hql = "SELECT businessId FROM json.Category WHERE name = :category_name";

				Query query = session.createQuery(hql);
				query.setParameter("category_name", category);
				List<Integer> businessIds = query.list();
				// System.out.println("businesses size = " +
				// businessIds.size());

				List<Integer> ids2 = new ArrayList<Integer>();
				for (int i = 0; i < businessIds.size(); i++) {
					if (!ids2.contains(businessIds.get(i))) {
						ids2.add(businessIds.get(i));
					}
				}
				System.out.println("businesses size = " + ids2.size());
				tx.commit();

				String hql2 = "";
				if (considerLocation) {
					hql2 = "SELECT businessId From json.Business b WHERE longitude = :lng AND latitude = :lat AND id IN :list_ids";
				} else {
					hql2 = "SELECT businessId From json.Business b WHERE id IN :list_ids";
				}
				tx = session.beginTransaction();
				Query query2 = session.createQuery(hql2);
				query2.setParameterList("list_ids", ids2);
				if (considerLocation) {
					query2.setParameter("lng", lng);
					query2.setParameter("lat", lat);
				}
				List<String> businessIdsStr = query2.list();

				tx.commit();

				tx = session.beginTransaction();

				String hql3 = "From json.Review WHERE stars = 5 AND businessId IN :list_ids";
				Query query3 = session.createQuery(hql3);
				query3.setParameterList("list_ids", businessIdsStr);
				List<Review> reviews = query3.list();
				tx.commit();

				result = (ArrayList<Review>) reviews;

				test.Writer.writeAllReviewsOfCategoryToFile(category, reviews);

			} catch (HibernateException e) {
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
		return result;
	}

	public static Object[] rankBusinesses(WordTopic wordTopic, LdaModel model,
			ArrayList<Review> reviews) {

		Object obj[] = new Object[2];

		ArrayList<Review> allReviews = GUIFunctions.getAllReviews();

		ArrayList<Business> businesses = new ArrayList<Business>();

		SessionFactory factory = getSessionFactory();
		if (factory != null) {
			Session session = factory.openSession();
			Transaction tx = null;
			try {
				double[][] theta = model.theta;
				int topicNum = wordTopic.getTopicNum();
				ArrayList<Integer> docsIndex = new ArrayList<Integer>();

				for (int i = 0; i < model.z.length; i++) {
					Arrays.sort(model.z[i]);
					int x = Arrays.binarySearch(model.z[i], topicNum);
					if (x > -1) {
						docsIndex.add(i);
					}
					// for (int j = 0; j < model.z[i].length; j++) {
					// if (model.z[i][j] == topicNum) {
					// docsIndex.add(i);
					// break;
					// }
					// }
				}

				// ArrayList<String> reviewIds = new ArrayList<String>();
				//
				// for (Review review : reviews) {
				// reviewIds.add(review.getReviewId());
				// }
				//
				// tx = session.beginTransaction();
				// for (int i = 0; i < docsIndex.size(); i++) {
				// String hql =
				// "UPDATE json.Review set topicValue = :topicValue "
				// + "WHERE reviewId = :reviewId";
				// Query query = session.createQuery(hql);
				// query.setParameter("topicValue",
				// theta[docsIndex.get(i)][topicNum]);
				// query.setParameter("reviewId", reviews.get(i).getReviewId());
				// query.executeUpdate();
				// }
				// tx.commit();
				// tx = session.beginTransaction();
				// String hql2 =
				// "SELECT SUM(R.topicValue) AS sumTopics, R.businessId, B.name FROM json.Review R "
				// + "INNER JOIN json.Business as B "
				// + "ON B.businessId = R.businessId "
				// + "WHERE R.reviewId IN :listReviewIds "
				// + "GROUP BY R.businessId , B.name ORDER BY sumTopics DESC";
				// Query query2 = session.createQuery(hql2);
				// query2.setParameterList("listReviewIds", reviewIds);
				// List<Object[]> results = query2.list();
				// tx.commit();
				//
				// result = (ArrayList) results;

				Map<String, Double> businessesValues = new HashMap<String, Double>();

				for (int i = 0; i < docsIndex.size(); i++) {
					Review review = allReviews.get(docsIndex.get(i));
					// review.setTopicValue(theta[docsIndex.get(i)][topicNum]);
					if (businessesValues.containsKey(review.getBusinessId())) {
						double prev = businessesValues.get(review
								.getBusinessId());
						businessesValues.put(review.getBusinessId(), prev
								+ theta[docsIndex.get(i)][topicNum]);
					} else {
						businessesValues.put(review.getBusinessId(),
								theta[docsIndex.get(i)][topicNum]);
					}
				}

//				businessesValues = sortByValue(businessesValues);
				businessesValues =
						businessesValues.entrySet().stream()
					     .sorted(Entry.comparingByValue())
					     .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
					                               (e1, e2) -> e1, LinkedHashMap::new));

				Set<String> businessesIds = businessesValues.keySet();

				tx = session.beginTransaction();
				String hql5 = "FROM json.Business B "
						+ "WHERE B.businessId IN :listBusinessIds ";
				Query query5 = session.createQuery(hql5);
				query5.setParameterList("listBusinessIds", businessesIds);
				List<Business> results5 = query5.list();
				tx.commit();

				// for (Object[] objects : results5) {
				// result.add(new Object[] {businessesValues.get(objects[0]),
				// objects[0], objects[1]});
				// }

				obj[0] = results5;
				obj[1] = businessesValues;

				GUIFunctions.setRankedBusinesses(obj);

				test.Writer.writeRankedBusinesses(obj, topicNum);

			} catch (HibernateException e) {
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}
		}

		return obj;

	}

	private static SessionFactory getSessionFactory() {
		SessionFactory factory = null;
		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
		return factory;
	}

	private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
