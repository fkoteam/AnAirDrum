package ru.maklas.mnet2;

/**
 * Package which is used to respond on ConnectionRequest.
 */
public class Response{

    final boolean accept;
    Object responseData;

    /**
     * Respond with success flag
     * @param data Your response data
     */
    public static Response accept(Object data){
        return new Response(true, data);
    }

    /**
     * Respond with refuse flag
     * @param data Your response data
     */
    public static Response refuse(Object data){
        return new Response(false, data);
    }

    private Response(boolean accept, Object responseData) {
        this.accept = accept;
        this.responseData = responseData;
    }

    public boolean accepted() {
        return accept;
    }

    public Object getResponseData() {
        return responseData;
    }

    public void setResponseData(Object responseData) {
        this.responseData = responseData;
    }
}
