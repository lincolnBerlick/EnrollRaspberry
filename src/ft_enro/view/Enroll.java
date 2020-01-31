/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ft_enro.view;

import com.nitgen.SDK.BSP.NBioBSPJNI;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;



/**
 *
 * @author Lincoln Berlick
 */
public class Enroll extends javax.swing.JFrame implements ActionListener{

    /**
     *  variáveis 
     */
      private NBioBSPJNI bsp;
      private NBioBSPJNI.WINDOW_OPTION m_bspWindowOption;
      private NBioBSPJNI.FIR_HANDLE [][] m_CaptureFIRs;
      private NBioBSPJNI.FIR_HANDLE m_EnrollFIR;      
      private Icon warnIcon;
      public  boolean controller = true;
      private Thread th;
      
      
    
    public  Enroll() {        
        setUndecorated(true);
       m_CaptureFIRs = new NBioBSPJNI.FIR_HANDLE[11][2];
       bsp = new NBioBSPJNI(); 
        warnIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/ft_enroll/imagens/circleT.png")));
       
        initComponents();        
        if(CheckError()){
            return;
        } else{
              bsp.OpenDevice();
              if(!CheckError())
                  mudar_status("Dispositivo Inicializado com Sucesso");
        }
        
        //this.setVisible(true);
        
      
        
        
        
        //aqui adicionar os listeners aos botões
        minimo_dir.addActionListener(this);
        minimo_esque.addActionListener(this);
        anelar_esque.addActionListener(this);
        medio_esque.addActionListener(this);
        indicador_esque.addActionListener(this);
        polegador_esque.addActionListener(this);
        polegador_dir.addActionListener(this);
        indicador_dir.addActionListener(this);
        medio_dir.addActionListener(this);
        anelar_dir.addActionListener(this);
        jButton1.addActionListener(this);
        
      
        
      
    }
    
    
    public NBioBSPJNI.FIR_HANDLE  EnrolInit(){
 
      this.setVisible(true);       
      Runnable run = new Runnable() {          
          @Override
             public void run() {         
                createTemplate();
            }
        };          
      th = new Thread(run);
      th.start();
        try {              
            th.join();  
        } catch (InterruptedException ex) {
            Logger.getLogger(Enroll.class.getName()).log(Level.SEVERE, null, ex);
        }           
        
        
        if ((Byte)GetFingerCount() == 0) {
           dispose();
           return bsp.new FIR_HANDLE();
        }
       
        dispose();
        return m_EnrollFIR;
    }
    
    
    
        
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
         JButton btn = (JButton) e.getSource();
        if(e.getSource() == minimo_esque ){     
            CapturaDedo(10,btn);                      
        } else if(e.getSource() == anelar_esque ){
           CapturaDedo(9,btn);                            
        } else if(e.getSource() == medio_esque ){ 
           CapturaDedo(8,btn); 
        } else if(e.getSource() == indicador_esque ){
           CapturaDedo(7,btn); 
        } else if(e.getSource() == polegador_esque ){
           CapturaDedo(6,btn); 
        } else if(e.getSource() == polegador_dir ){
           CapturaDedo(1,btn); 
        } else if(e.getSource() == indicador_dir ){      
           CapturaDedo(2,btn); 
        } else if(e.getSource() == medio_dir ){
           CapturaDedo(3,btn); 
        } else if(e.getSource() == anelar_dir ){
           CapturaDedo(4,btn); 
        } else if(e.getSource() == minimo_dir){
           CapturaDedo(5,btn);            
        } else if(e.getSource() == jButton1){     
           sinc();
        }   
    }
    
    synchronized void sinc(){        
         controller = false;
          notify();
    }
    
    //Captura digital adicionar ao index 2 dedos em cada captura ** necessário para dar merge
    private void CapturaDedo(int dedo, JButton btn){
        for (int s = 0 ; s < 2 ; s++) {
            NBioBSPJNI.FIR_HANDLE hCaptureFIR  = bsp.new FIR_HANDLE();
            bsp.Capture(NBioBSPJNI.FIR_PURPOSE.VERIFY, hCaptureFIR, -1, null, m_bspWindowOption);
            if (!CheckError()){
                if (m_CaptureFIRs[dedo][s] != null) {
                    m_CaptureFIRs[dedo][s].dispose();
                    m_CaptureFIRs[dedo][s] = null;
                }
                m_CaptureFIRs[dedo][s] = hCaptureFIR;                
            }
         }  
              
        
                mudar_status("Capture success INDEX[ " + dedo + " ]");
                btn.setIcon(warnIcon);
     }
    
    //Converte TextEncode para FIR_HANDLE
    public NBioBSPJNI.FIR_TEXTENCODE FirToText(NBioBSPJNI.FIR_HANDLE fir){
        
        NBioBSPJNI.FIR_TEXTENCODE  text = bsp.new FIR_TEXTENCODE();
        bsp.GetTextFIRFromHandle(fir, text);
        return text;
        
    }
    
    //Convert input_FIR para TextEncode
    public NBioBSPJNI.INPUT_FIR TextToFir(NBioBSPJNI.FIR_TEXTENCODE fir){
        
        NBioBSPJNI.INPUT_FIR inputfir = bsp.new INPUT_FIR();         
        inputfir.SetTextFIR(fir);
        return inputfir;
       
         
        
    }
    
        //Converte FIR para Fir_Handle
    public NBioBSPJNI.FIR HandleToFir(NBioBSPJNI.FIR_HANDLE fir){

      NBioBSPJNI.FIR inputfir = bsp.new FIR();         
      bsp.GetFIRFromHandle(fir, inputfir);
      return inputfir;
       
         
        
    }
    
    //Convert InputFir para FIR
    public NBioBSPJNI.INPUT_FIR FirToInputFir(NBioBSPJNI.FIR fir){
        
     NBioBSPJNI.INPUT_FIR inputfir = bsp.new INPUT_FIR();         
     inputfir.SetFullFIR(fir);
     return inputfir;       
    }
        
        
    //Verifica existência de erros no módulo BSP
    public Boolean CheckError() {
        if (bsp.IsErrorOccured())  {
            mudar_status("NBioBSP Error Occured [" + bsp.GetErrorCode() + "]");
            return true;
        }
        return false;
    }
    public void mudar_status(String text){
        jl_status.setText(text);
    }

        
    //Itinerar dedos
    private byte GetFingerCount() {
        byte nCount = 0;
        for (int i = 0 ; i < 10 ; i++) {
            if (m_CaptureFIRs != null && m_CaptureFIRs[i][0] != null && m_CaptureFIRs[i][1] != null) {
                    nCount++;
            }
        }
        return nCount;
    }
        
        
    public synchronized void createTemplate(){
            
        while(controller){
            try {                
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Enroll.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      
        byte byfingerCount = GetFingerCount();
        byte byConvType = NBioBSPJNI.EXPORT_MINCONV_TYPE.FDU;        
        if (byfingerCount == 0) {
            m_CaptureFIRs = null;
            
           mudar_status("Not Exist Capture Data");
           return;
        }       
        
        NBioBSPJNI.Export exportEngine = bsp.new Export();
        NBioBSPJNI.Export.DATA exportData = exportEngine.new DATA();
        exportData.EncryptType = byConvType;
        exportData.SamplesPerFinger = 2;
        exportData.DefaultFingerID = 0;
        exportData.FingerNum = byfingerCount;
        exportData.FingerData = new NBioBSPJNI.Export.FINGER_DATA[byfingerCount];
        boolean bStop = false;
        int nFingerIndex = 0;
        NBioBSPJNI.INPUT_FIR inputFIR = bsp.new INPUT_FIR();        
        for (int nCaptureIndex = 0 ; nCaptureIndex < 10 ; nCaptureIndex++) {	
			
            if (m_CaptureFIRs[nCaptureIndex][0] != null && m_CaptureFIRs[nCaptureIndex][1] != null) {
                exportData.FingerData[nFingerIndex] = exportEngine.new FINGER_DATA();
                exportData.FingerData[nFingerIndex].FingerID = (byte)(nCaptureIndex + 1);
                exportData.FingerData[nFingerIndex].Template = new NBioBSPJNI.Export.TEMPLATE_DATA[2];
                for (int s = 0 ; s < 2 ; s++) {
                    inputFIR.SetFIRHandle(m_CaptureFIRs[nCaptureIndex][s]);
                    NBioBSPJNI.Export.DATA exportCaptureData = exportEngine.new DATA();					
                    exportEngine.ExportFIR(inputFIR, exportCaptureData, byConvType);

                    if (CheckError()) {
                            bStop = true;
                            break;
                    }
                exportData.FingerData[nFingerIndex].Template[s] = exportEngine.new TEMPLATE_DATA();
                exportData.FingerData[nFingerIndex].Template[s].Data = exportCaptureData.FingerData[0].Template[0].Data;                  
            }
            nFingerIndex++;
            }
            if (bStop) break;
        }
        if (m_EnrollFIR != null) {
            m_EnrollFIR.dispose();
            m_EnrollFIR = null;
        }
        m_EnrollFIR = bsp.new FIR_HANDLE();       
        exportEngine.ImportFIR(exportData, m_EnrollFIR);       
        if (!CheckError())  {
            mudar_status("Template Criado Com Sucesso variavel em sincronized");
        }
        
       
       
        inputFIR = null;

        
        
        
        }
  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        minimo_dir = new javax.swing.JButton();
        minimo_esque = new javax.swing.JButton();
        anelar_esque = new javax.swing.JButton();
        medio_esque = new javax.swing.JButton();
        indicador_esque = new javax.swing.JButton();
        polegador_esque = new javax.swing.JButton();
        polegador_dir = new javax.swing.JButton();
        indicador_dir = new javax.swing.JButton();
        medio_dir = new javax.swing.JButton();
        anelar_dir = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jl_status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(true);
        setPreferredSize(new java.awt.Dimension(410, 405));
        setResizable(false);
        setSize(new java.awt.Dimension(393, 396));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel3.setRequestFocusEnabled(false);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Cadastro"));
        jPanel2.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jPanel2.setMinimumSize(new java.awt.Dimension(276, 240));
        jPanel2.setPreferredSize(new java.awt.Dimension(522, 436));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        minimo_dir.setBackground(new java.awt.Color(255, 255, 255));
        minimo_dir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/circleV.png"))); // NOI18N
        minimo_dir.setBorder(null);
        jPanel2.add(minimo_dir, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 70, 18, 18));

        minimo_esque.setBackground(new java.awt.Color(255, 255, 255));
        minimo_esque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/circleV.png"))); // NOI18N
        minimo_esque.setBorder(null);
        jPanel2.add(minimo_esque, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 18, 18));

        anelar_esque.setBackground(new java.awt.Color(255, 255, 255));
        anelar_esque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/circleV.png"))); // NOI18N
        anelar_esque.setBorder(null);
        jPanel2.add(anelar_esque, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 18, 18));

        medio_esque.setBackground(new java.awt.Color(255, 255, 255));
        medio_esque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/circleV.png"))); // NOI18N
        medio_esque.setBorder(null);
        jPanel2.add(medio_esque, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, 18, 18));

        indicador_esque.setBackground(new java.awt.Color(255, 255, 255));
        indicador_esque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/circleV.png"))); // NOI18N
        indicador_esque.setBorder(null);
        indicador_esque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                indicador_esqueActionPerformed(evt);
            }
        });
        jPanel2.add(indicador_esque, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 50, 18, 18));

        polegador_esque.setBackground(new java.awt.Color(255, 255, 255));
        polegador_esque.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/circleV.png"))); // NOI18N
        polegador_esque.setBorder(null);
        jPanel2.add(polegador_esque, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 100, 18, 18));

        polegador_dir.setBackground(new java.awt.Color(255, 255, 255));
        polegador_dir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/circleV.png"))); // NOI18N
        polegador_dir.setBorder(null);
        jPanel2.add(polegador_dir, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 100, 18, 18));

        indicador_dir.setBackground(new java.awt.Color(255, 255, 255));
        indicador_dir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/circleV.png"))); // NOI18N
        indicador_dir.setBorder(null);
        jPanel2.add(indicador_dir, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, 18, 18));

        medio_dir.setBackground(new java.awt.Color(255, 255, 255));
        medio_dir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/circleV.png"))); // NOI18N
        medio_dir.setBorder(null);
        jPanel2.add(medio_dir, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 40, 18, 18));

        anelar_dir.setBackground(new java.awt.Color(255, 255, 255));
        anelar_dir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/circleV.png"))); // NOI18N
        anelar_dir.setBorder(null);
        jPanel2.add(anelar_dir, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 50, 18, 18));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/mao.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, -1, 180));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ft_enroll/imagens/logo200x37.png"))); // NOI18N

        jButton1.setText("Finalizar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 183, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(39, 39, 39))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(17, 17, 17)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 278, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(30, 30, 30))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(45, 45, 45)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(60, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jl_status.setText("Status:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jl_status, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(399, Short.MAX_VALUE)
                .addComponent(jl_status)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 32, Short.MAX_VALUE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
	     
    }//GEN-LAST:event_jButton1ActionPerformed

    private void indicador_esqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_indicador_esqueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_indicador_esqueActionPerformed


    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton anelar_dir;
    private javax.swing.JButton anelar_esque;
    private javax.swing.JButton indicador_dir;
    private javax.swing.JButton indicador_esque;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jl_status;
    private javax.swing.JButton medio_dir;
    private javax.swing.JButton medio_esque;
    private javax.swing.JButton minimo_dir;
    private javax.swing.JButton minimo_esque;
    private javax.swing.JButton polegador_dir;
    private javax.swing.JButton polegador_esque;
    // End of variables declaration//GEN-END:variables


    
}
