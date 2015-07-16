package elective.close;

import javax.jws.WebMethod;

public interface Close {

	@WebMethod(operationName = "b") boolean b();
    
    @WebMethod 
    public String getState();
}
