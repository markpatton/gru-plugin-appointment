package fr.paris.lutece.plugins.appointment.business.planning;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.appointment.business.UtilDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Week Definition objects
 * 
 * @author Laurent Payen
 *
 */
public final class WeekDefinitionDAO extends UtilDAO implements IWeekDefinitionDAO
{

    private static final String SQL_QUERY_NEW_PK = "SELECT max(id_week_definition) FROM appointment_week_definition";
    private static final String SQL_QUERY_INSERT = "INSERT INTO appointment_week_definition (id_week_definition, date_of_apply, id_form) VALUES (?, ?, ?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE appointment_week_definition SET date_of_apply = ?, id_form = ? WHERE id_week_definition = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM appointment_week_definition WHERE id_week_definition = ?";
    private static final String SQL_QUERY_SELECT_COLUMNS = "SELECT id_week_definition, date_of_apply, id_form FROM appointment_week_definition";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECT_COLUMNS + " WHERE id_week_definition = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM = SQL_QUERY_SELECT_COLUMNS + " WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_OF_APPLY = SQL_QUERY_SELECT_BY_ID_FORM + " AND date_of_apply = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_FORM_AND_CLOSEST_TO_DATE_OF_APPLY = SQL_QUERY_SELECT_BY_ID_FORM
            + " AND date_of_apply <= ? ORDER BY date_of_apply DESC LIMIT 1";

    @Override
    public synchronized void insert( WeekDefinition weekDefinition, Plugin plugin )
    {
        weekDefinition.setIdWeekDefinition( getNewPrimaryKey( SQL_QUERY_NEW_PK, plugin ) );
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_INSERT, weekDefinition, plugin, true );
        executeUpdate( daoUtil );
    }

    @Override
    public void update( WeekDefinition weekDefinition, Plugin plugin )
    {
        DAOUtil daoUtil = buildDaoUtil( SQL_QUERY_UPDATE, weekDefinition, plugin, false );
        executeUpdate( daoUtil );
    }

    @Override
    public void delete( int nIdWeekDefinition, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdWeekDefinition );
        executeUpdate( daoUtil );
    }

    @Override
    public WeekDefinition select( int nIdWeekDefinition, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        WeekDefinition weekDefinition = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
            daoUtil.setInt( 1, nIdWeekDefinition );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                weekDefinition = buildWeekDefinition( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return weekDefinition;
    }

    @Override
    public List<WeekDefinition> findByIdForm( int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        List<WeekDefinition> listWeekDefinition = new ArrayList<>( );
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listWeekDefinition.add( buildWeekDefinition( daoUtil ) );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return listWeekDefinition;
    }

    @Override
    public WeekDefinition findByIdFormAndDateOfApply( int nIdForm, LocalDate dateOfApply, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        WeekDefinition weekDefinition = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_AND_DATE_OF_APPLY, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setDate( 2, Date.valueOf( dateOfApply ) );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                weekDefinition = buildWeekDefinition( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return weekDefinition;
    }

    @Override
    public WeekDefinition findByIdFormAndClosestToDateOfApply( int nIdForm, LocalDate dateOfApply, Plugin plugin )
    {
        DAOUtil daoUtil = null;
        WeekDefinition weekDefinition = null;
        try
        {
            daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_FORM_AND_CLOSEST_TO_DATE_OF_APPLY, plugin );
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setDate( 2, Date.valueOf( dateOfApply ) );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                weekDefinition = buildWeekDefinition( daoUtil );
            }
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
        return weekDefinition;
    }

    /**
     * Build a WeekDefinition business object from the resultset
     * 
     * @param daoUtil
     *            the prepare statement util object
     * @return a new WeekDefinition with all its attributes assigned
     */
    private WeekDefinition buildWeekDefinition( DAOUtil daoUtil )
    {
        int nIndex = 1;
        WeekDefinition weekDefinition = new WeekDefinition( );
        weekDefinition.setIdWeekDefinition( daoUtil.getInt( nIndex++ ) );
        weekDefinition.setSqlDateOfApply( daoUtil.getDate( nIndex++ ) );
        weekDefinition.setIdForm( daoUtil.getInt( nIndex ) );
        return weekDefinition;
    }

    /**
     * Build a daoUtil object with the WeekDefinition business object
     * 
     * @param query
     *            the query
     * @param weekDefinition
     *            the Week Definition
     * @param plugin
     *            the plugin
     * @param isInsert
     *            true if it is an insert query (in this case, need to set the id). If false, it is an update, in this case, there is a where parameter id to
     *            set
     * @return a new daoUtil with all its values assigned
     */
    private DAOUtil buildDaoUtil( String query, WeekDefinition weekDefinition, Plugin plugin, boolean isInsert )
    {
        int nIndex = 1;
        DAOUtil daoUtil = new DAOUtil( query, plugin );
        if ( isInsert )
        {
            daoUtil.setInt( nIndex++, weekDefinition.getIdWeekDefinition( ) );
        }
        daoUtil.setDate( nIndex++, weekDefinition.getSqlDateOfApply( ) );
        daoUtil.setInt( nIndex++, weekDefinition.getIdForm( ) );
        if ( !isInsert )
        {
            daoUtil.setInt( nIndex, weekDefinition.getIdWeekDefinition( ) );
        }
        return daoUtil;
    }

    /**
     * Execute a safe update (Free the connection in case of error when execute the query)
     * 
     * @param daoUtil
     *            the daoUtil
     */
    private void executeUpdate( DAOUtil daoUtil )
    {
        try
        {
            daoUtil.executeUpdate( );
        }
        finally
        {
            if ( daoUtil != null )
            {
                daoUtil.free( );
            }
        }
    }

}