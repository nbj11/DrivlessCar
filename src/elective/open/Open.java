package elective.open;

import javax.jws.WebMethod;

public interface Open {

	@WebMethod(operationName = "a") boolean a();
    
    @WebMethod 
    public String getState();
}
