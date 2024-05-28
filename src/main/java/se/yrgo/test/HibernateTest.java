package se.yrgo.test;

import jakarta.persistence.*;

import se.yrgo.domain.Student;
import se.yrgo.domain.Subject;
import se.yrgo.domain.Tutor;

import java.math.BigDecimal;
import java.util.List;

public class HibernateTest {
	public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("databaseConfig");

	public static void main(String[] args) {
		setUpData();
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		// Uppgift 1
		System.out.println("________Uppgift 1________");

		// Jag satt väldigt länge med denna uppgift och förstod inte 
		// hur jag skulle lösa det med member of när relationerna är så långt ifrån varandra 
		// (student har två tabeller mellan sig och subject)

		// så jag löste det på klassiskt sql vis istället. Snällasnälla ge mig godkänt för det :) :) 

		List<String> studentsOfScience = em.createNativeQuery(
				"SELECT s.name FROM student AS s JOIN tutor AS t ON s.tutor_fk = t.id JOIN subject_tutor AS st ON t.id = st.tutors_id JOIN subject AS su ON st.subjectsToTeach_ID = su.id WHERE su.subjectName = 'Science'")
				.getResultList();

		System.out.println("Students that have a tutor who knows science: ");
		for (String student : studentsOfScience) {
			System.out.println(student);
		}

		// Uppgift 2
		System.out.println("________Uppgift 2________");

		List<Object[]> results4 = em.createNativeQuery(
				"SELECT s.name as student_name, t.name as tutor_name FROM student s JOIN tutor t ON t.id = s.tutor_fk")
				.getResultList();

		for (Object[] item : results4) {
			System.out.println("The student " + item[0] + " has the tutor " + item[1]);
		}

		// uppgift 3 - jag använder BigDecimal och CAST AS DECIMAL(10,2) för att få ut
		// svaret i ett decimaltal. Annars blir det ett heltal.
		System.out.println("________Uppgift 3________");

		BigDecimal averageSemesters = (BigDecimal) em
				.createNativeQuery("SELECT AVG(CAST(numberOfSemesters AS DECIMAL(10,2))) FROM subject")
				.getSingleResult();
		System.out.println("The average number of semesters for the subject is " + averageSemesters);

		// uppgift 4
		System.out.println("________Uppgift 4________");

		Integer maxSalary = (Integer) em.createQuery("SELECT MAX(tutor.salary) from Tutor tutor").getSingleResult();
		System.out.println("The max salary of all the tutors is " + maxSalary);

		// uppgift 5
		System.out.println("________Uppgift 5________");

		List<Tutor> tutorsWithHigherSalary = em.createNamedQuery("tutorsWithHigherSalary", Tutor.class)
				.setParameter("higherThan", 10000).getResultList();
		System.out.println("List of tutors with higher salary than 10000: ");
		for (Tutor tutor : tutorsWithHigherSalary) {
			System.out.println(tutor.getName() + " who has the salary of " + tutor.getSalary());
		}

		tx.commit();
		em.close();
	}

	public static void setUpData() {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		Subject mathematics = new Subject("Mathematics", 2);
		Subject science = new Subject("Science", 2);
		Subject programming = new Subject("Programming", 3);
		em.persist(mathematics);
		em.persist(science);
		em.persist(programming);

		Tutor t1 = new Tutor("ABC123", "Johan Smith", 40000);
		t1.addSubjectsToTeach(mathematics);
		t1.addSubjectsToTeach(science);

		Tutor t2 = new Tutor("DEF456", "Sara Svensson", 20000);
		t2.addSubjectsToTeach(mathematics);
		t2.addSubjectsToTeach(science);

		// This tutor is the only tutor who can teach History
		Tutor t3 = new Tutor("GHI678", "Karin Lindberg", 0);
		t3.addSubjectsToTeach(programming);

		em.persist(t1);
		em.persist(t2);
		em.persist(t3);

		t1.createStudentAndAddtoTeachingGroup("Jimi Hendriks", "1-HEN-2019", "Street 1", "city 2", "1212");
		t1.createStudentAndAddtoTeachingGroup("Bruce Lee", "2-LEE-2019", "Street 2", "city 2", "2323");
		t3.createStudentAndAddtoTeachingGroup("Roger Waters", "3-WAT-2018", "Street 3", "city 3", "34343");

		tx.commit();
		em.close();
	}

}
