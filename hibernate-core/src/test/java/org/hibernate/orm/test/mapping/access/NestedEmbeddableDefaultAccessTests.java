/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.mapping.access;

import java.util.List;

import org.hibernate.mapping.BasicValue;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Property;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.DomainModelScope;
import org.hibernate.testing.orm.junit.FailureExpected;
import org.hibernate.testing.orm.junit.Jira;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
@DomainModel( annotatedClasses = { NestedEmbeddableDefaultAccessTests.MyEntity.class } )
@SessionFactory( exportSchema = false )
public class NestedEmbeddableDefaultAccessTests {
	@Test
	public void verifyEmbeddedMapping(DomainModelScope scope) {
		scope.withHierarchy( MyEntity.class, (descriptor) -> {
			final Property outerEmbedded = descriptor.getProperty( "outerEmbeddable" );
			verifyMapping( (Component) outerEmbedded.getValue() );
		} );
	}

	@Test
	@Jira( "https://hibernate.atlassian.net/browse/HHH-14063" )
	public void verifyElementCollectionMapping(DomainModelScope scope) {
		scope.withHierarchy( MyEntity.class, (descriptor) -> {
			final Property outerEmbeddedList = descriptor.getProperty( "outerEmbeddableList" );
			verifyMapping( (Component) ( (Collection) outerEmbeddedList.getValue() ).getElement() );
		} );
	}

	private void verifyMapping(Component outerEmbeddable) {
		final Property outerData = outerEmbeddable.getProperty( "outerData" );
		final BasicValue outerDataMapping = (BasicValue) outerData.getValue();
		final Property nestedEmbedded = outerEmbeddable.getProperty( "nestedEmbeddable" );
		final Component nestedEmbeddable = (Component) nestedEmbedded.getValue();
		final Property nestedData = nestedEmbeddable.getProperty( "nestedData" );
		final BasicValue nestedDataMapping = (BasicValue) nestedData.getValue();

		assertThat( outerDataMapping.getColumn().getText() ).isEqualTo( "outer_data" );
		assertThat( outerDataMapping.getJpaAttributeConverterDescriptor() ).isNotNull();

		assertThat( nestedDataMapping.getColumn().getText() ).isEqualTo( "nested_data" );
	}

	@Entity( name = "MyEntity" )
	@Table( name = "MyEntity" )
	public static class MyEntity {
	    @Id
	    private Integer id;
	    @Basic
		private String name;
		@Embedded
		private OuterEmbeddable outerEmbeddable;
		@ElementCollection
		private List<OuterEmbeddable> outerEmbeddableList;

		private MyEntity() {
			// for use by Hibernate
		}

		public MyEntity(Integer id, String name) {
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public OuterEmbeddable getOuterEmbeddable() {
			return outerEmbeddable;
		}

		public void setOuterEmbeddable(OuterEmbeddable outerEmbeddable) {
			this.outerEmbeddable = outerEmbeddable;
		}
	}

	@Embeddable
	public static class OuterEmbeddable {
		@Convert( converter = SillyConverter.class )
		@Column( name = "outer_data" )
		private String outerData;

		@Embedded
		private NestedEmbeddable nestedEmbeddable;
	}

	@Embeddable
	public static class NestedEmbeddable {
		@Convert( converter = SillyConverter.class )
		@Column( name = "nested_data" )
		private String nestedData;
	}
}
