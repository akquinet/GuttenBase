package de.akquinet.jbosscc.guttenbase.configuration.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import de.akquinet.jbosscc.guttenbase.hints.TableNameMapperHint;
import de.akquinet.jbosscc.guttenbase.mapping.TableNameMapper;
import de.akquinet.jbosscc.guttenbase.meta.ColumnMetaData;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;

/**
 * Implementation for MS Server SQL data base.
 * 
 * <p>
 * &copy; 2012 akquinet tech@spree
 * </p>
 * 
 * @Uses-Hint {@link TableNameMapperHint}
 * @author M. Dahm
 */
public class MsSqlTargetDatabaseConfiguration extends DefaultTargetDatabaseConfiguration {
  private static final long serialVersionUID = 1L;

  public MsSqlTargetDatabaseConfiguration(final ConnectorRepository connectorRepository) {
    super(connectorRepository);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initializeTargetConnection(final Connection connection, final String connectorId) throws SQLException {
    connection.setAutoCommit(false);

    disableTableForeignKeys(connection, connectorId, getTableMetaData(connectorId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void finalizeTargetConnection(final Connection connection, final String connectorId) throws SQLException {
    enableTableForeignKeys(connection, connectorId, getTableMetaData(connectorId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void beforeInsert(final Connection connection, final String connectorId, final TableMetaData table) throws SQLException {
    setIdentityInsert(connection, connectorId, true, table);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void afterInsert(final Connection connection, final String connectorId, final TableMetaData table) throws SQLException {
    setIdentityInsert(connection, connectorId, false, table);
  }

  private List<TableMetaData> getTableMetaData(final String connectorId) throws SQLException {
    return _connectorRepository.getDatabaseMetaData(connectorId).getTableMetaData();
  }

  private void disableTableForeignKeys(final Connection connection, final String connectorId, final List<TableMetaData> tableMetaData)
      throws SQLException {
    setTableForeignKeys(connection, connectorId, tableMetaData, false);
  }

  private void enableTableForeignKeys(final Connection connection, final String connectorId, final List<TableMetaData> tableMetaData)
      throws SQLException {
    setTableForeignKeys(connection, connectorId, tableMetaData, true);
  }

  private void setTableForeignKeys(final Connection connection, final String connectorId, final List<TableMetaData> tableMetaDatas,
      final boolean enable) throws SQLException {
    final TableNameMapper tableNameMapper = _connectorRepository.getConnectorHint(connectorId, TableNameMapper.class).getValue();

    for (final TableMetaData tableMetaData : tableMetaDatas) {
      final String tableName = tableNameMapper.mapTableName(tableMetaData);

      executeSQL(connection, "ALTER TABLE " + tableName + (enable ? " CHECK CONSTRAINT ALL" : " NOCHECK CONSTRAINT ALL"));
    }
  }

  private void setIdentityInsert(final Connection connection, final String connectorId, final boolean enable,
      final TableMetaData tableMetaData) throws SQLException {
    final TableNameMapper tableNameMapper = _connectorRepository.getConnectorHint(connectorId, TableNameMapper.class).getValue();
    final String tableName = tableNameMapper.mapTableName(tableMetaData);

    if (hasIdentityColumn(tableMetaData)) {
      executeSQL(connection, "SET IDENTITY_INSERT " + tableName + " " + (enable ? "ON" : "OFF"));
    }
  }

  private boolean hasIdentityColumn(final TableMetaData tableMetaData) {
    for (final ColumnMetaData columnMetaData : tableMetaData.getColumnMetaData()) {
      if (columnMetaData.getColumnTypeName().contains("IDENTITY")) {
        return true;
      }
    }

    return false;
  }
}