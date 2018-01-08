package freeplaneGTD

import freeplaneGTD.GTDMapReader
import org.freeplane.plugin.script.proxy.Proxy

class NodeState {
    HashMap<STATE_NEW, ArrayList<String>> StateIconsNew
    HashMap<STATE_DONE, String> StateIconsDone
    freeplaneGTD.GTDMapReader r
    
    enum STATE_NEW {
        ILLEGAL,     // some other combination
        NONE,        // nothing
        TASK_LATER,  // !
        //TASK_WEEK,   // !week - TBD add later
        TASK_TODAY,  // !*
        PROJECT      // project
    }

    enum STATE_DONE {
        ILLEGAL,
        NOT_DONE,
        DONE,
        CANCELED 
    }
    
    NodeState(Proxy.Controller c) {
        this.r = freeplaneGTD.GTDMapReader.instance
        // FYI: need this to have icons filled!
        def root = c.getSortedSelection(true)[0].map.root
        r.findIcons(root)
        this.StateIconsNew =  [(STATE_NEW.NONE):[],
                               (STATE_NEW.TASK_TODAY):[r.iconNextAction, r.iconToday],
                               (STATE_NEW.TASK_LATER):[r.iconNextAction],
                               (STATE_NEW.PROJECT):[r.iconProject]]
        this.StateIconsDone = [(STATE_DONE.NOT_DONE):null,
                               (STATE_DONE.DONE):r.iconDone,
                               (STATE_DONE.CANCELED):r.iconCancel]
    }
    
    STATE_NEW getNodeStateNew(Proxy.Node n) {
        def have = new HashSet<String>(n.icons.icons)
        def res = StateIconsNew.find{
            def exp = new HashSet<String>(it.value)
            exp.equals(have)
        }
        if (res) return res.key
        return STATE_NEW.ILLEGAL // for illegal cases
    }

    STATE_DONE getNodeStateDone(Proxy.Node n) {
        def have = n.icons.icons
        def res = StateIconsDone.findAll{
            have.contains(it.value)
        }
        if (res.size() == 0) return STATE_DONE.NOT_DONE
        if (res.size() > 1) return STATE_DONE.ILLEGAL
        return res.keySet()[0]
    }

    STATE_NEW getNextStateNew(STATE_NEW state) {
        switch (state) {
        case STATE_NEW.NONE:       return STATE_NEW.TASK_LATER
        case STATE_NEW.TASK_LATER: return STATE_NEW.TASK_TODAY
        // TBD: add week
        case STATE_NEW.TASK_TODAY: return STATE_NEW.PROJECT
        case STATE_NEW.PROJECT:    return STATE_NEW.TASK_LATER
        case STATE_NEW.ILLEGAL:    return STATE_NEW.TASK_LATER
        }
    }

    STATE_DONE getNextStateDone(STATE_DONE state) {
        switch (state) {
        case STATE_DONE.NOT_DONE: return STATE_DONE.DONE;
        case STATE_DONE.DONE:     return STATE_DONE.CANCELED;
        case STATE_DONE.CANCELED: return STATE_DONE.NOT_DONE;
        }
    }

    void setIconsForStateNew(Proxy.Node n, STATE_NEW state) {
        n.icons.clear()
        def newIcons = StateIconsNew[state]
        n.icons.addAll(newIcons)
    }

    void setIconsForStateDone(Proxy.Node n, STATE_DONE state) {
        [r.iconDone, r.iconCancel].each{ n.icons.remove( it ) }
        if (StateIconsDone[state]) n.icons.add(StateIconsDone[state])
    }
   
    void fillNextStateNew(n) {
        def currState = getNodeStateNew(n[0])
        def nextState = getNextStateNew(currState)
        n.each {
            setIconsForStateNew(it, nextState)
        }
    }

    void fillNextStateDone(n) {
        def currState = getNodeStateDone(n[0])
        def nextState = getNextStateDone(currState)
        n.each {
            setIconsForStateDone(it, nextState)
        }
    }
}
