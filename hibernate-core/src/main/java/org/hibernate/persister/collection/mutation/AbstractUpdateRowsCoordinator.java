/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html.
 */
package org.hibernate.persister.collection.mutation;

import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import static org.hibernate.sql.model.ModelMutationLogging.MODEL_MUTATION_LOGGER;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractUpdateRowsCoordinator implements UpdateRowsCoordinator {
	private final CollectionMutationTarget mutationTarget;
	private final SessionFactoryImplementor sessionFactory;

	public AbstractUpdateRowsCoordinator(CollectionMutationTarget mutationTarget, SessionFactoryImplementor sessionFactory) {
		this.mutationTarget = mutationTarget;
		this.sessionFactory = sessionFactory;
	}

	@Override
	public String toString() {
		return "UpdateRowsCoordinator(" + getMutationTarget().getRolePath() + ")";
	}

	public SessionFactoryImplementor getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public CollectionMutationTarget getMutationTarget() {
		return mutationTarget;
	}

	@Override
	public void updateRows(Object key, PersistentCollection<?> collection, SharedSessionContractImplementor session) {
		MODEL_MUTATION_LOGGER.tracef( "Updating collection rows - %s#%s", mutationTarget.getRolePath(), key );

		// update all the modified entries
		int count = doUpdate( key, collection, session );

		MODEL_MUTATION_LOGGER.debugf( "Updated `%s` collection rows - %s#%s", count, mutationTarget.getRolePath(), key );
	}

	protected abstract int doUpdate(Object key, PersistentCollection<?> collection, SharedSessionContractImplementor session);
}
