void doWork()
{
   try
   {
       Connection conn = ConnectionFactory.getConnection();
       PreparedStatement stmt = conn.preparedStatement("some query"); // executes a valid query
       ResultSet rs = stmt.executeQuery();
       while(rs.hasNext())
       {
          ... process the result set
       }
   }
   catch(SQLException sqlEx)
   {
       log(sqlEx);
   }
}
