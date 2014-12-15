package org.cyk.system.facebookextension.restfb;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.time.DateUtils;
import org.cyk.system.facebookextension.tools.api.post.Application;
import org.cyk.system.facebookextension.tools.api.post.Collector;
import org.cyk.system.facebookextension.tools.api.post.Group;
import org.cyk.system.facebookextension.tools.api.post.Manager;
import org.cyk.system.facebookextension.tools.provider.restfb.RestFB;
import org.cyk.utility.common.RunnableListener;
import org.cyk.utility.common.cdi.BeanListener;

import com.restfb.DefaultFacebookClient;
import com.restfb.exception.FacebookOAuthException;

public class ManagerSwingApplication extends JFrame implements RunnableListener<Collector>,ActionListener,BeanListener {

	private static final long serialVersionUID = 6340447425980325359L;
	
	private Manager manager;
	private Application application = new Application("147625215390534", "108693eea0a8c3b78e5d4819ff58c51c",null);
	
	private JTabbedPane tabbedPane = new JTabbedPane();
	private JTextField accessTokenField=new JTextField(),fromCreationDateField=new JTextField();
	private JTextArea console = new JTextArea();
	private JButton startButton=new JButton("Click to Start");
	 
	
	public ManagerSwingApplication() {
		super("Facebook Post Collector");
		//setLayout(new GridBagLayout());
		setSize(1000, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		manager = new Manager(new File("H:/fbposts"));
		manager.getCollector().getCollectorListeners().add(new RestFB());
		manager.getCollector().setGroups(Group.SOURCE_SELL_BUY);
		
		getContentPane().add(tabbedPane);
		initBusinessTab();
		initSettingsTab();
		
	}
	
	private void initSettingsTab(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(new JLabel("Access Token"),new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(accessTokenField,new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		tabbedPane.addTab("Settings", panel);
	}
	
	private void initBusinessTab(){
		JPanel panel = new JPanel(new GridBagLayout());
		
		panel.add(new JLabel("From Creation Date"),new GridBagConstraints(0, 0, 1, 0, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		panel.add(fromCreationDateField,new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		panel.add(startButton,new GridBagConstraints(0, 1, 3, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		startButton.addActionListener(this);
		
		JScrollPane scrollPane = new JScrollPane(console);
		panel.add(scrollPane,new GridBagConstraints(0, 2, 3, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		tabbedPane.addTab("Console", panel);
	}
	
	/**/
	
	public void started(Collector collector, Long time) {};
	
	@Override
	public void stopped(Collector collector, Long time) {
		
	}
	
	@Override
	public void throwable(Collector collector, Throwable throwable) {
		if(throwable instanceof FacebookOAuthException)
			JOptionPane.showMessageDialog(this, "Authentication exception\r\n"+throwable.toString());
	}
	
	@Override
	public void info(String message) {
		console(message);
	}
	
	private void console(final String message){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				console.append("\r\n"+message);
			}
		});
	}
	/*
	@Override
	public void started(Collector postCollector) {
		startButton.setText("Click to Stop");
	}
	
	@Override
	public void stopped(Collector postCollector) {
		startButton.setText("Click to Start");
	}
	*/
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==startButton){
			if(Boolean.TRUE.equals(manager.getCollector().getStop())){
				try {
					application.setAccessToken(accessTokenField.getText());
					manager.setFacebookClient(new DefaultFacebookClient(accessTokenField.getText()));
					manager.getCollector().getPeriod().setFromDate(DateUtils.parseDate(fromCreationDateField.getText(), "dd/MM/yyyy"));
					manager.getCollector().setLimit(1L);
				} catch (ParseException e1) {
					info(e1.toString());
				}
				manager.getCollector().getPeriod().setToDate(null);
				manager.getCollector().setStop(Boolean.FALSE);
				new Thread(manager.getCollector()).start();
				
			}else
				manager.getCollector().setStop(Boolean.TRUE);
		}else
			JOptionPane.showMessageDialog(this, "");
	}
	
	/**/
	
	public static void main(String[] args) {
		
		ManagerSwingApplication application = new ManagerSwingApplication();
		application.setVisible(Boolean.TRUE);
	}

	

}
