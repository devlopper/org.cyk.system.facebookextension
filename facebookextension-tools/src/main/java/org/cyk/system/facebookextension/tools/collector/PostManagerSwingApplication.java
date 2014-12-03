package org.cyk.system.facebookextension.tools.collector;

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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.time.DateUtils;
import org.cyk.utility.common.cdi.BeanListener;

public class PostManagerSwingApplication extends JFrame implements PostCollectorListener,ActionListener,BeanListener {

	private static final long serialVersionUID = 6340447425980325359L;
	
	private PostCollector postCollector = new PostCollector();
	private Application application = new Application("147625215390534", "108693eea0a8c3b78e5d4819ff58c51c",AbstractCollector.TEST_AT);
	
	private JTextField accessTokenField=new JTextField(),fromCreationDateField=new JTextField();
	private JTextArea console = new JTextArea();
	private JButton startButton=new JButton("Click to Start"),tokenInfosButton=new JButton("Infos");
	
	
	public PostManagerSwingApplication() {
		super("Facebook Post Collector");
		setSize(1000, 600);
		setLocationRelativeTo(null);
		startButton.addActionListener(this);
		tokenInfosButton.addActionListener(this);
		postCollector.getPostCollectorListeners().add(this);
		postCollector.setApplication(application);
		postCollector.setGroups(Group.SOURCE_SELL_BUY);
		postCollector.setDirectory(new File("H:/fbposts"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());
		
		
		
		getContentPane().add(new JLabel("Access Token"),new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(accessTokenField,new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(tokenInfosButton,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		getContentPane().add(new JLabel("From Creation Date"),new GridBagConstraints(0, 1, 1, 0, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		getContentPane().add(fromCreationDateField,new GridBagConstraints(1, 1, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		getContentPane().add(startButton,new GridBagConstraints(0, 2, 3, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		
		JScrollPane scrollPane = new JScrollPane(console);
		getContentPane().add(scrollPane,new GridBagConstraints(0, 3, 3, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}
	
	/**/
	
	@Override
	public void processPosts(PostCollector postCollector) {
		console(postCollector.getPosts().size()+" posts should be written to file.");
	}
	
	@Override
	public void authenticationException(PostCollector postCollector) {
		JOptionPane.showMessageDialog(this, "Authentication exception\r\n"+postCollector.getAuthenticationException().toString());
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
	
	@Override
	public void started(PostCollector postCollector) {
		startButton.setText("Click to Stop");
	}
	
	@Override
	public void stopped(PostCollector postCollector) {
		startButton.setText("Click to Start");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==startButton){
			if(Boolean.TRUE.equals(postCollector.getStop())){
				try {
					application.setAccessToken(accessTokenField.getText());
					postCollector.setFromCreationDate(DateUtils.parseDate(fromCreationDateField.getText(), "dd/MM/yyyy"));
				} catch (ParseException e1) {
					info(e1.toString());
				}
				postCollector.setToCreationDate(null);
				postCollector.setStop(Boolean.FALSE);
				new Thread(postCollector).start();
				
			}else
				postCollector.setStop(Boolean.TRUE);
		}else
			JOptionPane.showMessageDialog(this, "");
	}
	
	/**/
	
	public static void main(String[] args) {
		
		PostManagerSwingApplication application = new PostManagerSwingApplication();
		application.setVisible(Boolean.TRUE);
	}

	

}
