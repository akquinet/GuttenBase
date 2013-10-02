package de.akquinet.jbosscc.guttenbase.configuration.impl;

import java.sql.Connection;
import java.sql.SQLException;

import de.akquinet.jbosscc.guttenbase.configuration.TargetDatabaseConfiguration;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;

/**
 * (Almost) empty implementation
 * 
 * <p>
 * &copy; 2012-2020 akquinet tech@spree
 * </p>
 * 
 * @author M. Dahm
 */
public abstract class DefaultTargetDatabaseConfiguration extends AbstractDatabaseConfiguration implements TargetDatabaseConfiguration {
  private boolean _mayCommit = true;

  public DefaultTargetDatabaseConfiguration(final ConnectorRepository connectorRepository) {
    super(connectorRepository);
  }

  /**
   * {@inheritDoc}
   * 
   * true by default
   */
  @Override
  public boolean isMayCommit() {
    return _mayCommit;
  }

  public void setMayCommit(final boolean mayCommit) {
    _mayCommit = mayCommit;
  }

  /**
   * Connection is set autocommit false.
   * 
   * {@inheritDoc}
   */
  @Override
  public void initializeTargetConnection(final Connection connection, final String connectorId) throws SQLException {
    if (connection.getAutoCommit()) {
      connection.setAutoCommit(false);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void finalizeTargetConnection(final Connection connection, final String connectorId) throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void beforeInsert(final Connection connection, final String connectorId, final TableMetaData table) throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void afterInsert(final Connection connection, final String connectorId, final TableMetaData table) throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void beforeNewRow(final Connection connection, final String connectorId, final TableMetaData table) throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void afterNewRow(final Connection connection, final String connectorId, final TableMetaData table) throws SQLException {
  }
}
