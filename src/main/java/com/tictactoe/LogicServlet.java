package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int index = getSelectedIndex(req);
        HttpSession session = req.getSession(true);
        Field field = extractField(session);
        Sign sign=field.getField().get(index);
        if(sign!=Sign.EMPTY){
            RequestDispatcher dispatcher= getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }
        field.getField().put(index, Sign.CROSS);
        if(checkWin(session, resp, field)){
            return;
        }
        int emptyFieldIndex= field.getEmptyFieldIndex();
        if(emptyFieldIndex>=0){
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if(checkWin(session, resp, field)){
                return;
            }
        }
        else {
            session.setAttribute("draw", true);
            List<Sign> data= field.getFieldData();
            session.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");
            return;
        }

        List<Sign> data = field.getFieldData();
        session.setAttribute("field", field);
        session.setAttribute("data", data);
        resp.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumber = click.chars().allMatch(Character::isDigit);
        return isNumber ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession session) {
        Object field = session.getAttribute("field");
        if (field.getClass() != Field.class) {
            session.invalidate();
            throw new RuntimeException("Session is broken");
        }
        return (Field) field;
    }
    private boolean checkWin(HttpSession session, HttpServletResponse httpServletResponse, Field field) throws IOException {
        Sign winner=field.checkWin();
        if(Sign.NOUGHT==winner||Sign.CROSS==winner){
            session.setAttribute("winner", winner);
            List<Sign> data=field.getFieldData();
            session.setAttribute("data", data);
            httpServletResponse.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
